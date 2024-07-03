import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import FeedScreen from './app/components/FeedScreen';
import DiaryScreen from './app/components/DiaryScreen';
import WriteOptionScreen from './app/components/WriteOptionScreen';
import MapScreen from './app/components/MapScreen';
import ProfileScreen from './app/components/ProfileScreen';
import AIWriteScreen from './app/components/AIWriteScreen';
import ManualWriteScreen from './app/components/ManualWriteScreen';
import PostScreen from './app/components/PostScreen';
import DetailScreen from './app/components/DetailScreen';
import ImagesOnlyScreen from './app/components/ImagesOnlyScreen';
import ProfileEditScreen from './app/components/ProfileEditScreen';
import NotificationSettingScreen from './app/components/NotificationSettingScreen';
import FAQScreen from './app/components/FAQScreen';
import ContactAdminScreen from './app/components/ContactAdminScreen';
import { AuthProvider } from './contexts/AuthContext';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

const MainTabNavigator = () => {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName;
          if (route.name === '피드') {
            iconName = focused ? 'home' : 'home-outline';
          } else if (route.name === '다이어리') {
            iconName = focused ? 'book' : 'book-outline';
          } else if (route.name === '글쓰기') {
            iconName = focused ? 'add-circle' : 'add-circle-outline';
          } else if (route.name === '지도') {
            iconName = focused ? 'location' : 'location-outline';
          } else if (route.name === '내 프로필') {
            iconName = focused ? 'person' : 'person-outline';
          }
          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: 'blue',
        tabBarInactiveTintColor: 'gray',
      })}
    >
      <Tab.Screen name="피드" component={FeedScreen} options={{ headerShown: false }} />
      <Tab.Screen name="다이어리" component={DiaryScreen} options={{ headerShown: false }} />
      <Tab.Screen name="글쓰기" component={WriteOptionScreen} options={{ title: '글쓰기', headerShown: false }} />
      <Tab.Screen name="지도" component={MapScreen} options={{ title: '지도', headerShown: false }} />
      <Tab.Screen name="내 프로필" component={ProfileScreen} options={{ headerShown: false }} />
    </Tab.Navigator>
  );
};

const App = () => {
  return (
    <AuthProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Main">
          <Stack.Screen name="Main" component={MainTabNavigator} options={{ headerShown: false }} />
          <Stack.Screen
            name="Post"
            component={PostScreen}
            options={({ navigation }) => ({
              title: '게시물',
              headerLeft: () => <Ionicons name="arrow-back" size={24} color="black" onPress={() => navigation.goBack()} />,
            })}
          />
          <Stack.Screen name="Detail" component={DetailScreen} options={{ headerShown: false }} />
          <Stack.Screen
            name="AIWrite"
            component={AIWriteScreen}
            options={({ navigation }) => ({
              title: 'AI로 글쓰기',
              headerLeft: () => <Ionicons name="arrow-back" size={24} color="black" onPress={() => navigation.goBack()} />,
            })}
          />
          <Stack.Screen
            name="ManualWrite"
            component={ManualWriteScreen}
            options={({ navigation }) => ({
              title: '직접 작성하기',
              headerLeft: () => <Ionicons name="arrow-back" size={24} color="black" onPress={() => navigation.goBack()} />,
            })}
          />
          <Stack.Screen
            name="ImagesOnly"
            component={ImagesOnlyScreen}
            options={({ navigation }) => ({
              title: '사진 모아보기',
              headerLeft: () => <Ionicons name="arrow-back" size={24} color="black" onPress={() => navigation.goBack()} />,
            })}
          />
          <Stack.Screen
            name="ProfileEdit"
            component={ProfileEditScreen}
            options={{ title: 'ProfileEdit' }}
          />
          <Stack.Screen
            name="NotificationSetting"
            component={NotificationSettingScreen}
            options={{ title: 'NotificationSetting' }}
          />
          <Stack.Screen
            name="FAQ"
            component={FAQScreen}
            options={{ title: 'FAQ' }}
          />
          <Stack.Screen
            name="ContactAdmin"
            component={ContactAdminScreen}
            options={{ title: '관리자 문의' }}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </AuthProvider>
  );
};

export default App;
