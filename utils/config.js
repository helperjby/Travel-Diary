import Constants from 'expo-constants';

const extra = Constants.manifest?.extra || {};

const {
  apiUrl,
  kakaoClientId,
  redirectUri,
  naverClientId,
  naverClientSecret,
  googleClientId,
  webClientId,
  googleMapsApiKey
} = extra;

export default {
  apiUrl,
  kakaoClientId,
  redirectUri,
  naverClientId,
  naverClientSecret,
  googleClientId,
  webClientId,
  googleMapsApiKey
};