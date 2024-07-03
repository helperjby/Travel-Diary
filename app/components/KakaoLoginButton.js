// import React, { useEffect } from 'react';
// import { View, Text, StyleSheet, TouchableWithoutFeedback, Image, Dimensions } from 'react-native';
// import { WebView } from 'react-native-webview';
// import { useDispatch, useSelector } from 'react-redux';
// import { setLoginStatus, setUserInfo } from '../../app/store';
// import config from '../../utils/config';

// const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${config.kakaoClientId}&redirect_uri=${config.redirectUri}&response_type=code`;
// const API_URL = `${config.apiUrl}/api/endpoint`;

// const KakaoLoginButton = ({ navigation }) => {
//   const dispatch = useDispatch();
//   const loginStatus = useSelector((state) => state.user.loginStatus);
//   const userInfo = useSelector((state) => state.user.userInfo);

//   useEffect(() => {
//     fetch(API_URL)
//       .then(response => response.json())
//       .then(data => {
//         console.log(data);
//       })
//       .catch(error => {
//         console.error('Error:', error);
//       });
//   }, []);

//   const login = () => {
//     dispatch(setLoginStatus(true));
//   };

//   const handleResponseFromKakaoLogin = async (state) => {
//     let url = state.url;
//     if (url.includes('code=')) {
//       const code = new URL(url).searchParams.get('code');

//       try {
//         const tokenResponse = await fetch('https://kauth.kakao.com/oauth/token', {
//           method: 'POST',
//           headers: {
//             'Content-Type': 'application/x-www-form-urlencoded',
//           },
//           body: `grant_type=authorization_code&client_id=${config.kakaoClientId}&redirect_uri=${config.redirectUri}&code=${code}`,
//         });
//         const tokenData = await tokenResponse.json();

//         const profileResponse = await fetch('https://kapi.kakao.com/v2/user/me', {
//           headers: {
//             Authorization: `Bearer ${tokenData.access_token}`,
//           },
//         });
//         const profileData = await profileResponse.json();

//         console.log('카카오 사용자 정보:', profileData);

//         dispatch(setLoginStatus(false));
//         dispatch(setUserInfo(profileData));

//         navigation.navigate('FeedScreen');
//       } catch (error) {
//         console.error('Error during Kakao login:', error);
//         dispatch(setLoginStatus(false));
//       }
//     }
//   };

//   return (
//     <View style={styles.container}>
//       {loginStatus ? (
//         <WebView
//           source={{ uri: KAKAO_AUTH_URL }}
//           style={styles.webView}
//           onNavigationStateChange={handleResponseFromKakaoLogin}
//         />
//       ) : (
//         <View>
//           {userInfo && (
//             <View>
//               <Text style={styles.result}>카카오 로그인 성공</Text>
//               <Text style={styles.result}>{userInfo.properties.nickname}</Text>
//               <Text style={styles.result}>{userInfo.kakao_account.email}</Text>
//             </View>
//           )}
//           <TouchableWithoutFeedback onPress={login}>
//             <Image source={require('../../assets/kakao-login-btn.png')} style={styles.loginBtn} />
//           </TouchableWithoutFeedback>
//         </View>
//       )}
//     </View>
//   );
// };

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     justifyContent: 'center',
//     alignItems: 'center',
//     backgroundColor: '#E5F4FF',
//   },
//   webView: {
//     width: Dimensions.get('window').width,
//     height: Dimensions.get('window').height,
//   },
//   loginBtn: {
//     width: 303,
//     height: 45,
//     borderRadius: 3,
//   },
//   result: {
//     fontSize: 16,
//     fontWeight: 'bold',
//     color: '#000',
//   },
// });

// export default KakaoLoginButton;