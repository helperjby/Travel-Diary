import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Image, StyleSheet, Alert, Platform } from 'react-native';
import * as ImagePicker from 'expo-image-picker';

const ProfileEditScreen = ({ navigation }) => {
  const [name, setName] = useState('');
  const [introduce, setIntroduce] = useState('');
  const [profileImage, setProfileImage] = useState(null);

  const pickImage = async () => {
    let permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (permissionResult.granted === false) {
      alert('사진 접근 권한을 허용해야 사진을 선택할 수 있습니다.');
      return;
    }

    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    if (!result.cancelled) {
      setProfileImage(result.uri);
    }
  };

  const handleLeave = () => {
    Alert.alert(
      '회원탈퇴',
      '정말 회원탈퇴 하시겠습니까?',
      [
        { text: '취소', style: 'cancel' },
        { text: '확인', onPress: () => performLeaveAction() },
      ],
      { cancelable: false }
    );
  };

  const performLeaveAction = async () => {
    try {
      const response = await fetch('/api/users', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        console.log('회원탈퇴 처리');
        navigation.navigate('Login');
      } else {
        console.error('회원탈퇴 처리 실패');
        alert('회원탈퇴 중 문제가 발생했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('회원탈퇴 처리 중 에러:', error);
      alert('회원탈퇴 중 문제가 발생했습니다. 다시 시도해주세요.');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>프로필 수정</Text>
      <View style={styles.imageContainer}>
        <TouchableOpacity onPress={pickImage}>
          {profileImage ? (
            <Image source={{ uri: profileImage }} style={styles.profileImage} />
          ) : (
            <Image source={require('../../assets/snack-icon.png')} style={styles.profileImage} />
          )}
        </TouchableOpacity>
        <Text style={styles.imageText}>나중에 언제든지 변경할 수 있습니다.</Text>
      </View>
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          onChangeText={setName}
          value={name}
          placeholder="사용자 닉네임"
          placeholderTextColor="#888"
        />
        <TextInput
          style={styles.input}
          onChangeText={setIntroduce}
          value={introduce}
          placeholder="한 줄 소개"
          placeholderTextColor="#888"
        />
      </View>
      <TouchableOpacity style={[styles.button, styles.saveButton]} onPress={() => console.log('수정하기')}>
        <Text style={styles.buttonText}>수정하기</Text>
      </TouchableOpacity>
      <TouchableOpacity style={[styles.button, styles.leaveButton]} onPress={handleLeave}>
        <Text style={[styles.buttonText, { color: 'gray' }]}>회원탈퇴</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingTop: Platform.OS === 'android' ? 25 : 0, // 안드로이드에서 StatusBar와 겹치는 문제 해결
    backgroundColor: 'white',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  imageContainer: {
    alignItems: 'center',
    marginBottom: 20,
  },
  profileImage: {
    width: 120,
    height: 120,
    borderRadius: 60,
  },
  imageText: {
    marginTop: 10,
    color: '#666',
    fontSize: 12,
  },
  inputContainer: {
    width: '80%',
    marginBottom: 20,
  },
  input: {
    height: 40,
    borderColor: '#ccc',
    borderWidth: 1,
    marginBottom: 10,
    paddingHorizontal: 10,
    backgroundColor: '#fff',
    borderRadius: 5,
    fontSize: 14,
    color: '#333',
  },
  button: {
    width: '80%',
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 10,
  },
  saveButton: {
    backgroundColor: '#007bff',
  },
  leaveButton: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: '#ccc',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default ProfileEditScreen;
