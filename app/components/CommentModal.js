import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Modal, KeyboardAvoidingView, Platform, TouchableWithoutFeedback, Keyboard } from 'react-native';

const CommentModal = ({ isVisible, onClose, replyTo }) => {
  const [newComment, setNewComment] = useState('');

  const handleAddCommentOrReply = () => {
    // 댓글 추가 로직을 여기에 추가하세요
    setNewComment('');
    onClose();
  };

  return (
    <Modal
      visible={isVisible}
      animationType="slide"
      transparent={true}
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View style={styles.modalContainer}>
          <KeyboardAvoidingView
            style={styles.modalContentWrapper}
            behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
            keyboardVerticalOffset={Platform.OS === 'ios' ? 60 : 0}
          >
            <TouchableWithoutFeedback onPress={onClose}>
              <View style={styles.modalBackdrop} />
            </TouchableWithoutFeedback>
            <View style={styles.modalContent}>
              <Text style={styles.modalHeader}>댓글 작성</Text>
              <TextInput
                style={styles.modalInput}
                placeholder="댓글을 입력해주세요."
                placeholderTextColor="#aaa"
                value={newComment}
                onChangeText={setNewComment}
                multiline={true}
              />
              <View style={styles.modalButtons}>
                <Button title="취소" onPress={onClose} />
                <Button title="등록" onPress={handleAddCommentOrReply} />
              </View>
            </View>
          </KeyboardAvoidingView>
        </View>
      </TouchableWithoutFeedback>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    justifyContent: 'flex-end',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContentWrapper: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  modalBackdrop: {
    flex: 1,
  },
  modalContent: {
    backgroundColor: 'white',
    padding: 20,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
  },
  modalHeader: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  modalInput: {
    height: 100,
    padding: 10,
    marginBottom: 10,
    textAlignVertical: 'top',
    borderWidth: 0, // 테두리 없애기
    backgroundColor: '#f9f9f9', // 입력 필드 배경색
    borderRadius: 5, // 모서리 둥글게
  },
  modalButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
});

export default CommentModal;