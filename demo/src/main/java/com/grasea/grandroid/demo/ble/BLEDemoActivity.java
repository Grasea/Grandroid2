package com.grasea.grandroid.demo.ble;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.GrandroidBle;
import com.grasea.grandroid.ble.GrandroidConnectionListener;
import com.grasea.grandroid.ble.controller.BleDevice;
import com.grasea.grandroid.ble.data.GattServiceChannelHandler;
import com.grasea.grandroid.ble.scanner.AutoConnectScanResultHandler;
import com.grasea.grandroid.demo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BLEDemoActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.button)
    Button btnSend;
    @BindView(R.id.editText)
    EditText editText;
    ArrayList<GattServiceChannelHandler> serviceArray = new ArrayList<>();
    RecyclerView.Adapter<ViewHolder> adapter;
    public String address;
    public HandShakeProtocol handShakeProtocol;

    @Override
    protected void onDestroy() {
        GrandroidBle.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.button)
    public void onClick() {
        handShakeProtocol.handshake();
//        Config.logd("send handshake:" + BleDataParserHelper.getStringDataParser().parse(handshake));
//        boolean send = GrandroidBle.with(address).findService(serviceUUID).getChannel(wUUID).send(handshake);
//        Config.loge("send success:" + send);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                Config.logi("action:" + action);
//            }
//        };
//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        ButterKnife.bind(this);
        btnSend.setEnabled(false);
        initRecycler();
        GrandroidBle.enableDebugLog(false);
        GrandroidBle.init(this, new GrandroidConnectionListener() {
            @Override
            public void onReadRssiValue(int rssiValue) {

            }

            @Override
            public void onCharacteristicWrite(int status) {

            }

            @Override
            public void onDeviceReady(@Nullable BleDevice controller) {
                Config.loge("發現並連上裝置:" + controller.getName() + "[" + controller.getAddress() + "]");
                ArrayList<GattServiceChannelHandler> serviceHandlers = controller.getServiceHandlers();
                serviceArray.clear();
                serviceArray.addAll(serviceHandlers);
                adapter.notifyDataSetChanged();
                address = controller.getAddress();
                handShakeProtocol = new HandShakeProtocol(address, editText.getText().toString());
                GrandroidBle.getInstance().getDeviceScanner().stopScan();
//                try {
//                    GrandroidBle.with(controller.getAddress()).findService(serviceUUID).getChannel(rUUID).startListenBleData();
//                    GrandroidBle.with(controller.getAddress()).findService(serviceUUID).getChannel("49535343-aca3-481c-91ec-d85e28a60318").startListenBleData();
//                } catch (NullPointerException e) {
//                    Config.loge(e);
//                }
//                for (GattServiceChannelHandler handler : serviceHandlers) {
//                    ArrayList<Channel> channels = handler.getChannels();
//                    for (Channel channel : channels) {
//
//                        Config.loge("[" + handler.getService().getUuid().toString() + "] " + channel.toString() + " can Listen:" + channel.isReadChannel() + ",can notification:" + channel.isNotificationChannel() + ", can write:" + channel.isSendChannel());
//                    }
//                }
                GrandroidBle.getInstance().bindName(handShakeProtocol);
                GrandroidBle.getInstance().bind(controller.getAddress(), handShakeProtocol);
                btnSend.setEnabled(true);
            }

            @Override
            public void onDeviceDisconnected(@Nullable BleDevice controller) {
                btnSend.setEnabled(false);
                GrandroidBle.getInstance().getDeviceScanner().startScan();
            }

            @Override
            public void onFailed(int errorCode) {

            }
        });
//        GrandroidBle.getInstance().getDeviceScanner().setRetry(10).filterName("OSERIO-FWP-511").setScanResultHandler(new AutoConnectScanResultHandler(60000)).startScan();
        GrandroidBle.getInstance().getDeviceScanner().setRetry(10).filterName("OSERIO-FWP-511").setScanResultHandler(new AutoConnectScanResultHandler(60000) {
            @Override
            public void onDeviceFailed(int errorCode) {
                Config.logi("onDeviceScan Filed:" + errorCode);
                super.onDeviceFailed(errorCode);
            }
        }).startScan();

    }

    public void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listitem_main, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.text.setText(serviceArray.get(position).getService().getUuid().toString());
            }

            @Override
            public int getItemCount() {
                return serviceArray.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.text)
        TextView text;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

    }
}
