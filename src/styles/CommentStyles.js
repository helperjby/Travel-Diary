import { StyleSheet } from 'react-native';

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
    alignSelf: 'stretch',  // 댓글을 부모의 너비에 맞춤
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
    fontSize: 12,
    fontWeight: 'bold',
  },
  replyDate: {
    fontSize: 10,
    color: 'gray',
    marginBottom: 2,
  },
  replyContent: {
    fontSize: 12,
  },
  replyButton: {
    alignSelf: 'flex-start',
    marginTop: 5,
    paddingVertical: 2,
    paddingHorizontal: 10,
    backgroundColor: '#d1d1d1',
    borderRadius: 5,
  },
  replyButtonText: {
    fontSize: 12,
    color: '#000',
  },
  commentInputContainer: {
    left: 0,
    right: 0,
    bottom: 15,
    backgroundColor: '#fff',
    padding: 10,
    flexDirection: 'row',
    alignItems: 'center',
    borderTopWidth: 1,
    borderColor: '#ddd',
  },
  commentInput: {
    flex: 1,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 5,
    padding: 10,
    marginRight: 10,
  },
});

export default styles;