package com.luciofm.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.gson.Gson;
import com.luciofm.presenter.bluetooth.BluetoothChatService;
import com.luciofm.presenter.bluetooth.DeviceListActivity;
import com.luciofm.presenter.model.PresenterMessage;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;

    private StringBuffer mOutStringBuffer;

    private Server mServer;
    private Connection mConnection;

    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.chronometer)
    Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mServer = new Server();
        Kryo kryo = new Kryo();
        kryo.register(PresenterMessage.class);

        mServer.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                mConnection = connection;
                Toast.makeText(MainActivity.this, "Connected...", Toast.LENGTH_SHORT).show();
            }
        });

        mServer.start();
        try {
            mServer.bind(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothChatService.REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
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
        Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        Log.e(TAG, "--- ON DESTROY ---");
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
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
                            getActionBar().setTitle(mConnectedDeviceName);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            getActionBar().setTitle("Connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            getActionBar().setTitle("Idle");
                            break;
                    }
                    break;
                case BluetoothChatService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "sent: " + writeMessage);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothChatService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "recvd: " + readMessage);
                    parseMessage(readMessage);
                    //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
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

    private void parseMessage(String message) {
        Gson gson = new Gson();

        PresenterMessage msg = gson.fromJson(message, PresenterMessage.class);

        String method = msg.getMethod();
        if (method.contentEquals("ping"))
            mChatService.write(pongMessage());
        else if (method.contentEquals("pong"))
            Toast.makeText(this, "PONG", Toast.LENGTH_SHORT).show();
        else if (method.contentEquals("text"))
            textMethod(msg);
    }

    private void textMethod(PresenterMessage msg) {
        text.setText(msg.getExtra());
    }

    private byte[] pongMessage() {
        Gson gson = new Gson();
        return gson.toJson(new PresenterMessage("pong"), PresenterMessage.class).getBytes();
    }

    private byte[] pingMessage() {
        Gson gson = new Gson();
        return gson.toJson(new PresenterMessage("ping"), PresenterMessage.class).getBytes();
    }

    private void sendCommand(String command) {
        Gson gson = new Gson();
        mChatService.write(gson.toJson(new PresenterMessage(command), PresenterMessage.class).getBytes());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case BluetoothChatService.REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case BluetoothChatService.REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
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

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, BluetoothChatService.REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, BluetoothChatService.REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            case R.id.menu_ping:
                mChatService.write(pingMessage());
                return true;
        }
        return false;
    }

    @OnClick({R.id.button_advance, R.id.button_back, R.id.button_next, R.id.button_prev})
    public void onButtonClick(Button button) {
        String command = button.getText().toString().toLowerCase();

        sendCommand(command);
    }

    /*public void showConnectTcpDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                try {
                    mServer.connect(15, value, 8080);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }*/
}
