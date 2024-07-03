import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';

const initialComments = [
  {
    id: '1',
    author: '여행좋아',
    content: '재미있는 여행이네요! 저도 가고싶어요',
    date: '2024-06-07',
    replies: [
      { id: '1-1', author: '여행매니아', content: '저도 가고싶어요!', date: '2024-06-08' }
    ]
  },
  {
    id: '2',
    author: '여행중독자',
    content: '좋은 글 감사합니다!',
    date: '2024-06-07',
    replies: []
  }
];

const Comments = ({ onCommentPress }) => {
  const [comments, setComments] = useState(initialComments);

  return (
    <View style={styles.container}>
      <ScrollView style={styles.commentSection}>
        <Text style={styles.commentHeader}>댓글</Text>
        {comments.map((comment) => (
          <TouchableOpacity key={comment.id} onPress={() => onCommentPress(comment.id)}>
            <View style={styles.commentItem}>
              <Text style={styles.commentAuthor}>{comment.author}</Text>
              <Text style={styles.commentDate}>{comment.date}</Text>
              <Text style={styles.commentContent}>{comment.content}</Text>
              {comment.replies.length > 0 && (
                <View style={styles.replySection}>
                  {comment.replies.map((reply) => (
                    <View key={reply.id} style={styles.replyItem}>
                      <Text style={styles.replyAuthor}>{reply.author}</Text>
                      <Text style={styles.replyDate}>{reply.date}</Text>
                      <Text style={styles.replyContent}>{reply.content}</Text>
                    </View>
                  ))}
                </View>
              )}
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  commentSection: {
    padding: 15,
  },
  commentHeader: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  commentItem: {
    marginBottom: 15,
    padding: 10,
    borderRadius: 5,
    backgroundColor: '#f9f9f9',
  },
  commentAuthor: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  commentDate: {
    fontSize: 12,
    color: 'gray',
    marginBottom: 5,
  },
  commentContent: {
    fontSize: 14,
    marginBottom: 5,
  },
  replySection: {
    paddingLeft: 15,
    marginTop: 10,
    backgroundColor: '#f1f1f1',
    borderRadius: 5,
  },
  replyItem: {
    marginBottom: 5,
    padding: 5,
    backgroundColor: '#e9e9e9',
    borderRadius: 5,
  },
  replyAuthor: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  replyDate: {
    fontSize: 12,
    color: 'gray',
    marginBottom: 2,
  },
  replyContent: {
    fontSize: 14,
  },
});

export default Comments;