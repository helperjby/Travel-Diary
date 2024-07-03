import React from 'react';
import { View, Image, StyleSheet } from 'react-native';
import NaverLoginButton from './NaverLoginButton';
// import KakaoLoginButton from './KakaoLoginButton';
// import GoogleLoginButton from './GoogleLoginButton';
import { useAuth } from '../../contexts/AuthContext';

const LoginScreen = ({ navigation }) => {
  const { login, isLoading } = useAuth();

  const handleLogin = async (authConfig) => {
    await login(authConfig);
  };

  //  const goToFeedScreen = () => {
  //   navigation.navigate('FeedScreen');
  // };

  return (
    <View style={styles.container}>
      <Image source={require('../../assets/logo.png')} style={styles.logo} />
      <Image source={require('../../assets/character.gif')} style={styles.character} />
      <View style={styles.buttonContainer}>
        <NaverLoginButton handleLogin={handleLogin} />
        <KakaoLoginButton handleLogin={handleLogin} />
        <GoogleLoginButton handleLogin={handleLogin} />
      </View>
      {/* <TouchableOpacity style={styles.button} onPress={goToFeedScreen}>
        <Text style={styles.buttonText}>Go to Feed</Text>
      </TouchableOpacity> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#E5F4FF',
  },
  logo: {
    width: 200,
    height: 50,
    marginBottom: 20,
  },
  character: {
    width: 150,
    height: 150,
    marginBottom: 40,
  },
  buttonContainer: {
    marginBottom: 20,
  },
});

export default LoginScreen;
