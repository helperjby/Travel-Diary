import React, { useEffect } from 'react';
import { View, StyleSheet, TouchableWithoutFeedback, Image } from 'react-native';
import * as AuthSession from 'expo-auth-session';
import { useAuth } from '../../contexts/AuthContext';

const NAVER_CLIENT_ID = 'your_naver_client_id';
const NAVER_CLIENT_SECRET = 'your_naver_client_secret';
const STATE = encodeURIComponent('randomState');
const REDIRECT_URI = encodeURIComponent('http://localhost:8080/oauth2/authorization/naver?redirect_uri=http://localhost:3000&mode=unlink');
const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${NAVER_CLIENT_ID}&state=${STATE}&redirect_uri=${REDIRECT_URI}`;

const NaverLoginButton = () => {
  const { login, navigation } = useAuth();
  const [request, result, promptAsync] = AuthSession.useAuthRequest(
    {
      clientId: NAVER_CLIENT_ID,
      redirectUri: REDIRECT_URI,
      scopes: ['profile', 'email'],
      responseType: 'token',
      state: STATE,
    },
    {
      authorizationEndpoint: 'https://nid.naver.com/oauth2.0/authorize',
      tokenEndpoint: 'https://nid.naver.com/oauth2.0/token',
    }
  );

  useEffect(() => {
    if (result?.type === 'success') {
      const url = result.url;
      if (url.includes('code=')) {
        const code = new URL(url).searchParams.get('code');
        fetchAccessToken(code);
      }
    }
  }, [result]);

  const fetchAccessToken = async (code) => {
    try {
      const tokenResponse = await fetch(`https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=${NAVER_CLIENT_ID}&client_secret=${NAVER_CLIENT_SECRET}&code=${code}&state=${STATE}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      });

      const tokenData = await tokenResponse.json();

      if (!tokenResponse.ok) {
        throw new Error('Failed to fetch access token from Naver');
      }

      const { access_token } = tokenData;
      fetchUserInfo(access_token);
    } catch (error) {
      console.error('Error during Naver login:', error);
    }
  };

  const fetchUserInfo = async (token) => {
    try {
      const profileResponse = await fetch('https://openapi.naver.com/v1/nid/me', {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!profileResponse.ok) {
        throw new Error('Failed to fetch user profile from Naver');
      }

      const profileData = await profileResponse.json();
      login({ user: profileData.response, provider: 'naver' });
      navigation.navigate('FeedScreen');
    } catch (error) {
      console.error('Error during Naver login:', error);
    }
  };

  return (
    <View style={styles.container}>
      <TouchableWithoutFeedback onPress={() => promptAsync()}>
        <Image source={require('../../assets/naver-login-btn.png')} style={styles.loginBtn} />
      </TouchableWithoutFeedback>
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
  loginBtn: {
    width: 303,
    height: 45,
    borderRadius: 3,
  },
});

export default NaverLoginButton;
