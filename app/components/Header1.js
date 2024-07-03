import React from 'react';
import { View, Text, Image, StyleSheet, Platform, StatusBar } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const Header1 = ({ title }) => {
  return (
    <View style={styles.header}>
      <Image source={require('../../assets/logoicon.png')} style={styles.logoIcon} />
      <Text style={styles.headerTitle}>{title}</Text>
      <Ionicons name="notifications-outline" size={24} color="black" />
    </View>
  );
};

const styles = StyleSheet.create({
  header: {
    width: '100%',
    height: Platform.OS === 'android' ? 60 + StatusBar.currentHeight : 60,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 15,
    backgroundColor: '#fff',
    paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight : 0,
  },
  logoIcon: {
    width: 30,
    height: 30,
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default Header1;