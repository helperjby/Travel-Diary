import React, { useState } from 'react';
import { View, Text, Image, ScrollView, StyleSheet, KeyboardAvoidingView, Platform, TouchableOpacity } from 'react-native';
import Comments from './Comments';
import CommentModal from './CommentModal';

const post = {
  id: '1',
  title: '끝내줬던 3박 4일 제주도 여행',
  image: 'https://via.placeholder.com/200', // 실제 이미지 URL로 대체
  date: '2024-06-07',
  content: `여행은 역시 아무나 하는게 아니야. 어딘가로 떠나는 것에 있어서 가장 중요한 것은 내가 뭘할지 정하는 것이다. 여행은 순간이고 기억에 남는 것은 뭘 했냐 뿐이니까. 그런데 어쩌지 나는 전형적인 MBTI 'p'라서 뭘할지 정하는 유형은 아니다.

오늘의 일기는 내가 제주도에서 어떤 일이 있었는지에 관한 고찰이다.

나는 지난 3일 제주도로 3박 4일 여정을 출발했다. 그곳에 도착하자마자 한 것은 그저 돌투더기길을 걷던 것이다. 제주도하면 삼다도 그중에서도 나는 돌이 가장 생뚱맞다고 생각한다. 돌이야 땅바닥을 파면 나오는 것이 아닌가?`,
  author: '여행중아'
};

const PostScreen = () => {
  const [isModalVisible, setModalVisible] = useState(false);
  const [replyTo, setReplyTo] = useState(null);

  const handleReply = (parentId) => {
    setReplyTo(parentId);
    setModalVisible(true);
  };

  const openModal = () => {
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
    setReplyTo(null);
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      keyboardVerticalOffset={Platform.OS === 'ios' ? 60 : 0} // iOS의 경우 키보드 높이 보정
    >
      <ScrollView contentContainerStyle={styles.scrollViewContent}>
        <Image source={{ uri: post.image }} style={styles.image} />
        <View style={styles.contentContainer}>
          <Text style={styles.title}>{post.title}</Text>
          <Text style={styles.meta}>글 공개 | {post.date}</Text>
          <Text style={styles.content}>{post.content}</Text>
        </View>
        <Comments onCommentPress={handleReply} />
      </ScrollView>
      <TouchableOpacity style={styles.addCommentButton} onPress={openModal}>
        <Text style={styles.addCommentButtonText}>댓글 남기기</Text>
      </TouchableOpacity>
      <CommentModal isVisible={isModalVisible} onClose={closeModal} replyTo={replyTo} />
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollViewContent: {
    flexGrow: 1,
    paddingBottom: 100,
  },
  image: {
    width: '100%',
    height: 200,
  },
  contentContainer: {
    padding: 15,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  meta: {
    fontSize: 14,
    color: 'gray',
    marginBottom: 10,
  },
  content: {
    fontSize: 16,
    lineHeight: 24,
  },
  addCommentButton: {
    backgroundColor: '#007AFF',
    padding: 15,
    alignItems: 'center',
  },
  addCommentButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default PostScreen;