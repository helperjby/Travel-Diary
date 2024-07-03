import React from 'react';
import { View, Text, Image, StyleSheet, FlatList, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

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

const DetailScreen = ({ route, navigation }) => {
  const { item } = route.params;

  const viewImagesOnly = () => {
    const images = dummyDiaryData.map(data => data.image);
    navigation.navigate('ImagesOnly', { images });
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Ionicons name="arrow-back" size={24} color="black" onPress={() => navigation.goBack()} />
        <Text style={styles.headerTitle}>여행 기록 1</Text>
        <Ionicons name="ellipsis-vertical" size={24} color="black" onPress={viewImagesOnly} />
      </View>
      <Text style={styles.detailTitle}>{item.title}</Text>
      <Text style={styles.detailDate}>{item.date}</Text>
      <Image source={item.image} style={styles.detailImage} />
      <ScrollView>
        {dummyDiaryData.map((data) => (
          <TouchableOpacity
            key={data.id}
            style={styles.card}
            onPress={() => navigation.navigate('Post', { id: data.id })}
          >
            <Image source={data.image} style={styles.image} />
            <View style={styles.cardContent}>
              <Text style={styles.title}>{data.title}</Text>
              <Text style={styles.date}>{data.date}</Text>
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};

DetailScreen.navigationOptions = {
  headerShown: false,
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
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
  detailTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    marginVertical: 10,
    marginLeft: 20,
  },
  detailDate: {
    fontSize: 16,
    color: 'gray',
    marginBottom: 10,
    marginLeft: 20,
  },
  detailImage: {
    width: '100%',
    height: 200,
    borderRadius: 10,
    marginBottom: 10,
  },
});

export default DetailScreen;