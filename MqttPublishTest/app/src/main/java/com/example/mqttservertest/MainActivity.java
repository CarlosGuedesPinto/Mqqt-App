package com.example.mqttservertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public String TAG = "ASD";
    MqttAndroidClient client;

    Button butOn, butOff, butReadValues, butClearText;
    EditText mqqtId, textOutputState, textOutputSubscribe, textOutputSubscribeHumidity;

    public String username = "evwclientes";
    public String password = "EVWwbMqTt";

    String idCharger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butOn = findViewById(R.id.butOn);
        butOff = findViewById(R.id.butOff);
        butReadValues = findViewById(R.id.butSubscribe);
        butClearText = findViewById(R.id.clearTextBut);

        mqqtId = findViewById(R.id.mqttId);
        textOutputState = findViewById(R.id.textOutputState);
        textOutputSubscribe = findViewById(R.id.textOutputReadedValues);
        textOutputSubscribeHumidity = findViewById(R.id.textOutputHumidityValues);

        butOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                idCharger = mqqtId.getText().toString();
                if (!idCharger.equals("")) {
                    connectMqttp(idCharger, "ON");
                }
                else {
                    Toast.makeText(getApplication(), "Campo de Id vazio!", Toast.LENGTH_LONG);
                }
            }
        });

        butOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                idCharger = mqqtId.getText().toString();

                if (!idCharger.equals("")) {
                    connectMqttp(idCharger, "OFF");
                }
                else {
                    Toast.makeText(getApplication(), "Campo de Id vazio!", Toast.LENGTH_LONG);
                }
            }
        });

        butReadValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idCharger = mqqtId.getText().toString();
                connectMqttp(idCharger, "subscribe");
            }
        });

        butClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textOutputState.setText("");
                textOutputSubscribe.setText("");
                client.unregisterResources();
                client.close();
            }
        });

    }


    private void subscribe(final String type, String id) {

        String topic = null;
        int qos = 0;

        if (type.equals("state")) {
            topic = username + "/" + id + "/switch/" + id + "boxenable/state";
        }
        else if (type.equals("voltage")) {
            topic = username + "/" + id + "/sensor/" + id + "pzem1/state";

            try {
                //Test connection with the Mqtt server
                if (client.isConnected() && !topic.equals(null)) {
                    client.subscribe(topic, qos);
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(final String topic, MqttMessage message) throws Exception {

                            final String payload = new String(message.getPayload());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textOutputSubscribe.setText(payload);
                                }
                            });


                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }
            } catch (Exception e) {
                Log.d(TAG,"Error :" + e);
            }
        }



    }

    private void subscribe1(final String type, String id) {

        String topic = null;
        int qos = 0;

        if (type.equals("state")) {
            topic = username + "/" + id + "/switch/" + id + "boxenable/state";
        }
        else if (type.equals("humidity")) {
            topic = username + "/" + id + "/sensor/" + id + "pzem1/state";

            try {
                //Test connection with the Mqtt server
                if (client.isConnected() && !topic.equals(null)) {
                    client.subscribe(topic, qos);
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(final String topic, MqttMessage message) throws Exception {

                            final String payload1 = new String(message.getPayload());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textOutputSubscribeHumidity.setText(payload1);
                                }
                            });
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }
            } catch (Exception e) {
                Log.d(TAG,"Error :" + e);
            }
        }

    }


    private void connectMqttp(final String idCharger, final String command) {
        Log.d(TAG, "connect");
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://89.152.208.73", clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        if (command.equals("subscribe")) {
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        //Subscribe to obtain Voltage, Current, Power and Energy Info
                        subscribe("voltage", idCharger);
                        //subscribe1("humidity", idCharger);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                        Log.d(TAG, exception.toString());
                    }

                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        //Subscribe to obtain Voltage, Current, Power and Energy Info
                        publish(command, idCharger);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                        Log.d(TAG, exception.toString());
                    }

                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    private void publish(String payloadReceived, String chargerId) {
        String topic = username + "/" + chargerId + "/switch/" + chargerId + "boxenable/set";
        String payload = payloadReceived;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            String msg = textOutputState.getText() + System.getProperty("line.separator") + payload;
            textOutputState.setText(msg);

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
