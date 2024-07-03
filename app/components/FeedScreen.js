import React, { useState, useRef } from 'react';
import { View, Text, Image, StyleSheet, FlatList, RefreshControl } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const initialData = [
  {
    id: '1',
    title: '끝내줬던 3박 4일 제주도 여행',
    image: 'https://example.com/jeju1.jpg',
    likes: 52,
    comments: 10,
    author: '여행중아',
  },
  // 나머지 데이터 생략
];

const FeedItem = ({ title, image, likes, comments, author }) => (
  <View style={styles.feedItem}>
    <Image source={{ uri: image }} style={styles.image} />
    <Text style={styles.title}>{title}</Text>
    <View style={styles.infoContainer}>
      <Text style={styles.author}>{author}</Text>
      <View style={styles.stats}>
        <Ionicons name="heart" size={16} color="red" />
        <Text style={styles.statsText}>{likes}</Text>
        <Ionicons name="chatbubble" size={16} color="gray" />
        <Text style={styles.statsText}>{comments}</Text>
      </View>
    </View>
  </View>
);

const FeedScreen = ({ navigation }) => {
  const [data, setData] = useState(initialData);
  const [refreshing, setRefreshing] = useState(false);
  const flatListRef = useRef(null);

  const onRefresh = () => {
    setRefreshing(true);
    setTimeout(() => {
      setRefreshing(false);
    }, 1000);
  };

  const loadMoreData = () => {
    const newData = data.concat(
      data.map((item, index) => ({
        ...item,
        id: `${data.length + index + 1}`,
        title: `더미 데이터 ${data.length + index + 1}`,
      }))
    );
    setData(newData);
  };

  const renderItem = ({ item }) => (
    <FeedItem
      id={item.id}
      title={item.title}
      image={item.image}
      likes={item.likes}
      comments={item.comments}
      author={item.author}
    />
  );

  navigation.addListener('tabPress', e => {
    flatListRef.current.scrollToOffset({ animated: true, offset: 0 });
  });

  return (
    <FlatList
      ref={flatListRef}
      data={data}
      renderItem={renderItem}
      keyExtractor={(item) => item.id}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      onEndReached={loadMoreData}
      onEndReachedThreshold={0.5}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  feedItem: {
    margin: 10,
    backgroundColor: '#fff',
    borderRadius: 10,
    overflow: 'hidden',
  },
  image: {
    width: '100%',
    height: 200,
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    padding: 10,
  },
  infoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 10,
  },
  author: {
    fontSize: 14,
    color: 'gray',
  },
  stats: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statsText: {
    marginLeft: 5,
    marginRight: 15,
    fontSize: 14,
  },
});

export default FeedScreen;
