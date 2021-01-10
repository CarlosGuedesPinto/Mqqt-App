import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet, Text, View, TextInput } from 'react-native';
import MQTT from 'sp-react-native-mqtt';

var message;

export default function App() {

  MQTT.createClient({

    uri: 'mqtt://89.152.208.73:1883',
    auth: 'true',
    pass: 'EvW2020eVw',
    user: 'evwallbox',
    clientId: '6c64e3e2-cc53-4979-aaea-3eec7e2c7abf',

  }).then(function (client) {
    console.log(client);
    //client.options.host = "89.152.208.73"
    client.on('closed', function () {
      console.log('mqtt.event.closed');
    });

    client.on('error', function (msg) {
      console.log('mqtt.event.error', msg);
    });

    client.on('message', function (msg) {
      //console.log('mqtt.event.message', msg);
      message = msg;
      console.log(message);
    });

    client.on('connect', function () {
      console.log('connected');
      var topic = 'evwclientes/6911848/sensor/6911848consumo/state'
      console.log(topic);
      client.subscribe(topic, 0);


    });

    client.connect();
  }).catch(function (err) {
    console.log(err);
  });


  return (
    <View style={styles.container}>
      <TextInput
        multiline={true}
        numberOfLines={4}>{this.message}</TextInput>
      <StatusBar style="auto" />
    </View>
  );

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
