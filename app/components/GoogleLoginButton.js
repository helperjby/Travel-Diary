// import React, { useEffect } from 'react';
// import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';
// import * as Google from 'expo-auth-session/providers/google';
// import { useDispatch } from 'react-redux';
// import { login } from '../../features/auth/authSlice';
// import config from '../../utils/config';

// const GoogleLoginButton = ({ navigation }) => {
//   const dispatch = useDispatch();

//   const [request, response, promptAsync] = Google.useAuthRequest({
//     expoClientId: config.googleClientId,
//     iosClientId: config.googleClientId,
//     androidClientId: config.googleClientId,
//     webClientId: config.webClientId,  // 여기서 webClientId를 설정합니다.
//     redirectUri: config.redirectUri,
//   });

//   useEffect(() => {
//     if (response?.type === 'success') {
//       const { authentication } = response;
//       if (authentication) {
//         fetchUserInfo(authentication.accessToken);
//       }
//     }
//   }, [response]);

//   const fetchUserInfo = async (token) => {
//     try {
//       const response = await fetch('https://www.googleapis.com/userinfo/v2/me', {
//         headers: { Authorization: `Bearer ${token}` },
//       });

//       const user = await response.json();
//       dispatch(login({ user, provider: 'google' }));
//       navigation.navigate('FeedScreen');
//     } catch (error) {
//       console.error('Error fetching user info:', error);
//     }
//   };

//   return (
//     <View>
//       <TouchableOpacity style={styles.button} onPress={() => promptAsync()}>
//         <Text style={styles.buttonText}>Sign in with Google</Text>
//       </TouchableOpacity>
//     </View>
//   );
// };

// const styles = StyleSheet.create({
//   button: {
//     backgroundColor: '#4285F4',
//     padding: 10,
//     borderRadius: 5,
//     alignItems: 'center',
//     marginVertical: 10,
//   },
//   buttonText: {
//     color: '#fff',
//     fontSize: 16,
//   },
// });

// export default GoogleLoginButton;