import React, { useState, useEffect } from 'react';
import { View, Text, Image, StyleSheet, FlatList, TouchableOpacity, Modal, KeyboardAvoidingView, Platform, BackHandler } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
import Header1 from './Header1'; // 헤더 컴포넌트 추가

const data = [
  {
    id: '1',
    title: '여행 기록 1',
    date: '2024.06.01 - 2024.06.05',
    image: require('../../assets/image11.jpg'), // 여행 이미지 경로 설정
  },
  {
    id: '2',
    title: '여행 기록 2',
    date: '2024.06.07 - 2024.06.08',
    image: require('../../assets/image11.jpg'), // 여행 이미지 경로 설정
  },
];

const dummyDiaryData = [
  {
    id: '1',
    title: '관내진 남성 3박 4일 제주도 여행',
    date: '2024.06.07',
    image: require('../../assets/image11.jpg'),
  },
  {
    id: '2',
    title: '전주 한옥 마을에서 2박 3일 보내기',
    date: '2024.06.07',
    image: require('../../assets/image11.jpg'),
  },
  // 더미 데이터 추가
];

const DiaryScreen = () => {
  const navigation = useNavigation();
  const [modalVisible, setModalVisible] = useState(false); // 모달의 가시성을 관리하는 상태
  const [selectedDiaryIds, setSelectedDiaryIds] = useState([]); // 선택된 일기 항목의 ID를 관리하는 상태

  useEffect(() => {
    const backAction = () => {
      navigation.goBack();
      return true;
    };

    const backHandler = BackHandler.addEventListener(
      'hardwareBackPress',
      backAction
    );

    return () => backHandler.remove();
  }, [navigation]);

  // 일기 항목 선택/해제 토글 함수
  const toggleSelectDiary = (id) => {
    if (selectedDiaryIds.includes(id)) {
      setSelectedDiaryIds(selectedDiaryIds.filter(diaryId => diaryId !== id));
    } else {
      setSelectedDiaryIds([...selectedDiaryIds, id]);
    }
  };

  // 메인 화면의 여행 기록 카드 렌더링 함수
  const renderItem = ({ item }) => (
    <TouchableOpacity style={styles.card} onPress={() => navigation.navigate('Detail', { item })}>
      <Image source={item.image} style={styles.image} />
      <View style={styles.cardContent}>
        <Text style={styles.title}>{item.title}</Text>
        <Text style={styles.date}>{item.date}</Text>
      </View>
    </TouchableOpacity>
  );

  // 플러스 버튼을 렌더링하는 함수
  const renderFooter = () => (
    <TouchableOpacity style={styles.addButton} onPress={() => setModalVisible(true)}>
      <Ionicons name="add-circle-outline" size={40} />
    </TouchableOpacity>
  );

  // 모달 내부의 일기 항목 렌더링 함수
  const renderDiaryItem = ({ item }) => (
    <TouchableOpacity
      style={[
        styles.card,
        selectedDiaryIds.includes(item.id) && styles.selectedCard
      ]}
      onPress={() => toggleSelectDiary(item.id)}
    >
      <Image source={item.image} style={styles.image} />
      <View style={styles.cardContent}>
        <Text style={styles.title}>{item.title}</Text>
        <Text style={styles.date}>{item.date}</Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <View style={styles.topPadding} />
      <Header1 title="다이어리" />
      <FlatList
        data={data}
        renderItem={renderItem}
        keyExtractor={item => item.id}
        ListFooterComponent={renderFooter}
      />
      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={() => {
          setModalVisible(!modalVisible);
        }}
      >
        <View style={styles.modalView}>
          <FlatList
            data={dummyDiaryData}
            renderItem={renderDiaryItem}
            keyExtractor={item => item.id}
          />
          <View style={styles.modalButtons}>
            <TouchableOpacity
              style={[styles.button, styles.cancelButton]}
              onPress={() => setModalVisible(!modalVisible)}
            >
              <Text style={styles.buttonText}>취소</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.button, styles.confirmButton]}
              onPress={() => {
                // 추가 버튼을 눌렀을 때의 동작 추가
                setModalVisible(!modalVisible);
              }}
            >
              <Text style={styles.buttonText}>추가</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </KeyboardAvoidingView>
  );
};

// 스타일 정의
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  topPadding: {
    paddingTop: Platform.OS === 'ios' ? 60 : 30,
  },
  card: {
    flexDirection: 'row',
    margin: 10,
    padding: 10,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    backgroundColor: '#fff',
    alignItems: 'center',
  },
  selectedCard: {
    borderColor: 'blue',
    borderWidth: 2,
  },
  image: {
    width: 80,
    height: 80,
    borderRadius: 10,
    marginRight: 10,
  },
  cardContent: {
    flex: 1,
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  date: {
    fontSize: 14,
    color: 'gray',
  },
  addButton: {
    alignItems: 'center',
    marginVertical: 20,
    backgroundColor: 'transparent', // 투명 배경색 추가
  },
  modalView: {
    flex: 1,
    backgroundColor: 'white',
    marginTop: 60,
    padding: 20,
    borderRadius: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  modalButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 20,
  },
  button: {
    flex: 1,
    padding: 10,
    borderRadius: 10,
    alignItems: 'center',
    marginHorizontal: 5,
  },
  cancelButton: {
    backgroundColor: 'red',
  },
  confirmButton: {
    backgroundColor: 'blue',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
  },
});

export default DiaryScreen;