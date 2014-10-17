package com.luciofm.droidcon.ifican.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luciofm.droidcon.ifican.IfICan;
import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.bluetooth.BluetoothChatService;
import com.luciofm.droidcon.ifican.model.PresenterMessage;
import com.luciofm.droidcon.ifican.util.MessageEvent;
import com.luciofm.droidcon.ifican.util.RemoteEvent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class StartActivity extends Activity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;

    private static final long DISCOVERABLE_DELAY = 60000;
    Handler handler = new Handler();

    @InjectView(R.id.button_start)
    View button_start;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothChatService.REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.inject(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        handler.postDelayed(checkDiscoverableRunner, DISCOVERABLE_DELAY);

        IfICan.getBusInstance().register(this);
    }

    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            makeDiscoverable();
        }
    }

    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothAdapter != null && mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.d(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        handler.removeCallbacks(checkDiscoverableRunner);
        Log.d(TAG, "--- ON DESTROY ---");
        IfICan.getBusInstance().unregister(this);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }

    private String mConnectedDeviceName;

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChatService.MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d(TAG, "Connected: " + mConnectedDeviceName);
                            sendConnectedMessage();
                            buttonStartClick(button_start);
                            handler.postDelayed(checkDiscoverableRunner, DISCOVERABLE_DELAY);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d(TAG, "Connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d(TAG, "Idle");
                            ensureDiscoverable();
                            break;
                    }
                    break;
                case BluetoothChatService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "sent: " + writeMessage);
                    break;
                case BluetoothChatService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "recvd: " + readMessage);
                    parseMessage(readMessage);
                    break;
                case BluetoothChatService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BluetoothChatService.DEVICE_NAME);
                    Log.d(TAG, "DEVICE: " + mConnectedDeviceName);
                    break;
                case BluetoothChatService.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothChatService.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void sendConnectedMessage() {
        Gson gson = new Gson();

        mChatService.write(gson.toJson(new PresenterMessage("text", "Connected..."),
                PresenterMessage.class).getBytes());
    }

    private void parseMessage(String message) {
        Gson gson = new Gson();

        PresenterMessage msg = gson.fromJson(message, PresenterMessage.class);

        String method = msg.getMethod();
        if (method.contentEquals("ping"))
            mChatService.write(pongMessage());
        else if (method.contentEquals("pong"))
            Toast.makeText(this, "PONG", Toast.LENGTH_SHORT).show();
        else {
            postCommand(method);
        }
    }

    private void postCommand(String method) {
        RemoteEvent event = null;

        if (method.contentEquals("prev"))
            event = new RemoteEvent(RemoteEvent.EVENT_PREV);
        else if (method.contentEquals("next"))
            event = new RemoteEvent(RemoteEvent.EVENT_NEXT);
        else if (method.contentEquals("back"))
            event = new RemoteEvent(RemoteEvent.EVENT_BACK);
        else if (method.contentEquals("advance"))
            event = new RemoteEvent(RemoteEvent.EVENT_ADVANCE);

        IfICan.getBusInstance().post(event);
    }

    private byte[] pongMessage() {
        Gson gson = new Gson();
        return gson.toJson(new PresenterMessage("pong"), PresenterMessage.class).getBytes();
    }

    private byte[] pingMessage() {
        Gson gson = new Gson();
        return gson.toJson(new PresenterMessage("ping"), PresenterMessage.class).getBytes();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case BluetoothChatService.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth not enabled, leaving...", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    Runnable checkDiscoverableRunner = new Runnable() {
        @Override
        public void run() {
            if (mChatService.getState() == BluetoothChatService.STATE_LISTEN ||
                mChatService.getState() == BluetoothChatService.STATE_NONE) {
                ensureDiscoverable();
            }

            handler.postDelayed(this, DISCOVERABLE_DELAY);
        }
    };

    @OnClick(R.id.button_start)
    public void buttonStartClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0,
                view.getWidth(), view.getHeight());

        startActivity(intent, options.toBundle());

        handler.removeCallbacks(checkDiscoverableRunner);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        sendMessage(event.getMessage());
    }

    private void sendMessage(String message) {
        Gson gson = new Gson();

        mChatService.write(gson.toJson(new PresenterMessage("text", message),
                PresenterMessage.class).getBytes());
    }
}
