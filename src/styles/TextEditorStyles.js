import { StyleSheet } from 'react-native';

export const TextEditorStyles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f8f8f8',
  },
  diaryList: {
    flex: 1,
  },
  diaryEntry: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 15,
    marginBottom: 15,
    backgroundColor: '#fff',
  },
  diaryTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  diaryDate: {
    fontSize: 14,
    color: '#888',
    marginBottom: 10,
  },
  diaryContent: {
    fontSize: 16,
    marginBottom: 10,
  },
  diaryImage: {
    width: 100,
    height: 100,
    borderRadius: 8,
    marginBottom: 10,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 15,
    borderRadius: 8,
    backgroundColor: '#fff',
    marginBottom: 20,
    textAlignVertical: 'top',
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  block: {
    width: '48%',
    height: 66, // 기존 크기의 2/3로 줄이기
    alignItems: 'center',
    justifyContent: 'center',
    padding: 15,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    backgroundColor: '#fff',
    marginBottom: 10,
  },
  dateBlock: {
    fontSize: 16,
    color: '#555',
    textAlign: 'center',
  },
  textInput: {
    width: '100%',
    height: '100%', // 부모 요소와 같은 높이로 설정
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 10,
    borderRadius: 8,
    backgroundColor: '#fff',
    textAlignVertical: 'top',
  },
  audioIcon: {
    width: '100%',
    height: '100%',
    resizeMode: 'contain',
  },
  richEditorContainer: {
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  richEditor: {
    minHeight: 150,
    padding: 5,
    backgroundColor: '#fff',
    marginBottom: 10,
  },
  removeButton: {
    position: 'absolute',
    top: 5,
    right: 5,
    padding: 5,
  },
  openModalButton: {
    padding: 15,
    borderRadius: 8,
    backgroundColor: '#4285F4',
    alignItems: 'center',
  },
  modalBackground: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'flex-end',
  },
  modalView: {
    width: '100%',
    height: '80%',
    backgroundColor: 'white',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 20,
    justifyContent: 'space-between',
  },
  confirmModalView: {
    width: '80%',
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 20,
    alignItems: 'center',
    alignSelf: 'center',
  },
  confirmText: {
    fontSize: 18,
    marginBottom: 20,
  },
  confirmButtonRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
  },
  deleteButton: {
    padding: 10,
  },
  saveTempButton: {
    padding: 10,
  },
  deleteButtonText: {
    color: 'red',
    fontSize: 16,
  },
  saveTempButtonText: {
    color: 'blue',
    fontSize: 16,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
  },
  closeButton: {
    zIndex: 1,
  },
  modalContent: {
    flex: 1,
    marginTop: 20,
  },
  iconRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  saveButton: {
    backgroundColor: '#4285F4',
    padding: 10,
    borderRadius: 8,
  },
  saveButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  image: {
    width: 66, // 기존 크기의 2/3로 줄이기
    height: 66, // 기존 크기의 2/3로 줄이기
    borderRadius: 8,
  },
});
