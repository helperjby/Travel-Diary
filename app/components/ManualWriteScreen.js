import React, { useState, useRef } from 'react';
import { View, Text, TextInput, TouchableOpacity, Modal, FlatList, Keyboard, TouchableWithoutFeedback, Image, Alert, PanResponder, KeyboardAvoidingView, Platform } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { launchImageLibrary } from 'react-native-image-picker';
import { RichEditor, RichToolbar } from 'react-native-pell-rich-editor';
import DateTimePicker from '@react-native-community/datetimepicker';
import { TextEditorStyles as styles } from '../../src/styles/TextEditorStyles';

const ManualWriteScreen = () => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [datePickerVisible, setDatePickerVisible] = useState(false);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [blocks, setBlocks] = useState([]);
  const [diaryEntries, setDiaryEntries] = useState([]);
  const [editingEntry, setEditingEntry] = useState(null);
  const [confirmVisible, setConfirmVisible] = useState(false);
  const [photo, setPhoto] = useState(null);
  const richText = useRef();

  const panResponder = useRef(
    PanResponder.create({
      onMoveShouldSetPanResponder: (evt, gestureState) => {
        return gestureState.dy > 20; // 아래로 스와이프 감지
      },
      onPanResponderMove: (evt, gestureState) => {
        if (gestureState.dy > 20) {
          Keyboard.dismiss();
        }
      },
    })
  ).current;

  const handleAddBlock = (type, uri = null, content = '') => {
    if (blocks.length >= 4) {
      Alert.alert('최대 4개의 요소만 추가할 수 있습니다.');
      return;
    }
    const newBlock = { id: Date.now().toString(), type, uri, content };
    setBlocks([...blocks, newBlock]);
  };

  const handleAddOrUpdateDateBlock = (date) => {
    const dateContent = date.toLocaleDateString();
    const existingDateBlockIndex = blocks.findIndex(block => block.type === 'date');

    if (existingDateBlockIndex !== -1) {
      const updatedBlocks = [...blocks];
      updatedBlocks[existingDateBlockIndex].content = dateContent;
      setBlocks(updatedBlocks);
    } else {
      handleAddBlock('date', null, dateContent);
    }
  };

  const handleRemoveBlock = (id) => {
    setBlocks(blocks.filter(block => block.id !== id));
  };

  const renderBlock = ({ item }) => (
    <View style={styles.block}>
      {item.type === 'image' && item.uri ? (
        <Image source={{ uri: item.uri }} style={styles.image} />
      ) : item.type === 'date' ? (
        <Text style={styles.dateBlock}>{item.content}</Text>
      ) : item.type === 'audio' ? (
        <Image source={require('../../assets/soundicon.png')} style={styles.audioIcon} />
      ) : (
        <TextInput
          style={styles.textInput}
          placeholder="내용을 입력하세요..."
          multiline
          onChangeText={(text) => handleTextChange(item.id, text)}
          value={item.content}
        />
      )}
      <TouchableOpacity onPress={() => handleRemoveBlock(item.id)} style={styles.removeButton}>
        <Ionicons name="close-circle" size={24} color="red" />
      </TouchableOpacity>
    </View>
  );

  const handleTextChange = (id, text) => {
    setBlocks(blocks.map(block => (block.id === id ? { ...block, content: text } : block)));
  };

  const handleImagePicker = () => {
    launchImageLibrary({ mediaType: 'photo' }, (response) => {
      if (response.didCancel) {
        console.log('User cancelled image picker');
      } else if (response.error) {
        console.log('ImagePicker Error: ', response.error);
      } else {
        setPhoto(response.assets[0]); // assuming response.assets contains the selected image
        handleAddBlock('image', response.assets[0].uri);
      }
    });
  };

  const handleSave = async () => {
    const dateBlockExists = blocks.some(block => block.type === 'date');
    if (!dateBlockExists) {
      Alert.alert('날짜 요소를 추가해 주세요.');
      return;
    }
    const richTextContent = richText.current?.getContentHtml();
    const diaryDto = {
      title,
      content: richTextContent,
      travel_date: selectedDate.toISOString().split('T')[0],
      start_date: selectedDate.toISOString().split('T')[0],
      final_date: selectedDate.toISOString().split('T')[0],
      is_public: true,
      url: "http://example.com/map"
    };

    const formData = new FormData();
    if (photo) {
      formData.append('photo', {
        uri: photo.uri,
        type: photo.type,
        name: photo.fileName,
      });
    }
    formData.append('diaryDto', JSON.stringify(diaryDto));

    try {
      console.log('Sending data to server...');
      const response = await fetch('http://ec2-3-232-53-65.compute-1.amazonaws.com:8080/api/diaries?userId=1', {
        method: 'POST',
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        body: formData,
      });

      const result = await response.json();
      console.log('Response from server:', result);
      if (result.success) {
        Alert.alert('저장 성공', '데이터가 성공적으로 저장되었습니다.');
        setModalVisible(false);
        setBlocks([]);
        setTitle('');
        setContent('');
        setSelectedDate(new Date());
        setEditingEntry(null);
      } else {
        Alert.alert('저장 실패', '데이터 저장에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error during save:', error);
      Alert.alert('저장 실패', '데이터 저장 중 오류가 발생했습니다.');
    }
  };

  const handleTemporarySave = () => {
    const richTextContent = richText.current?.getContentHtml();
    const entry = {
      title,
      content: richTextContent,
      blocks,
      date: selectedDate
    };

    if (editingEntry !== null) {
      const updatedEntries = diaryEntries.map(entry =>
        entry.id === editingEntry.id
          ? { ...entry, title, content: richTextContent, blocks, date: selectedDate }
          : entry
      );
      setDiaryEntries(updatedEntries);
    } else {
      const newEntry = {
        id: Date.now().toString(),
        title,
        content: richTextContent,
        blocks,
        date: selectedDate
      };
      setDiaryEntries([...diaryEntries, newEntry]);
    }

    setModalVisible(false);
    setBlocks([]);
    setTitle('');
    setContent('');
    setSelectedDate(new Date());
    setEditingEntry(null);

    Alert.alert('임시 저장됨', '콘텐츠가 임시 저장되었습니다.');
  };

  const handleEditEntry = (entry) => {
    setEditingEntry(entry);
    setTitle(entry.title);
    setContent(entry.content);
    setBlocks(entry.blocks);
    setSelectedDate(new Date(entry.date));
    setModalVisible(true);
  };

  const handleCloseModal = () => {
    const hasContent = title || content || blocks.length > 0;
    if (hasContent) {
      setConfirmVisible(true);
    } else {
      setModalVisible(false);
    }
  };

  const handleConfirmClose = (action) => {
    if (action === 'delete') {
      console.log('콘텐츠가 삭제됩니다.');
      setModalVisible(false);
      setBlocks([]);
      setTitle('');
      setContent('');
      setSelectedDate(new Date());
      setEditingEntry(null);
    } else if (action === 'save') {
      handleTemporarySave();
    }
    setConfirmVisible(false);
  };

  const handleDateChange = (event, date) => {
    setDatePickerVisible(false);
    if (date) {
      setSelectedDate(date);
      handleAddOrUpdateDateBlock(date);
    }
  };

    const renderDiaryEntry = ({ item }) => (
    <TouchableOpacity onPress={() => handleEditEntry(item)} style={styles.diaryEntry}>
      <Text style={styles.diaryTitle}>{item.title}</Text>
      <Text style={styles.diaryDate}>{new Date(item.date).toLocaleDateString()}</Text>
      {item.blocks.map((block, index) => (
        block.type === 'image' && block.uri ? (
          <Image key={index} source={{ uri: block.uri }} style={styles.diaryImage} />
        ) : block.type === 'date' ? (
          <Text key={index} style={styles.diaryContent}>{block.content}</Text>
        ) : block.type === 'audio' ? (
          <Image key={index} source={require('../../assets/soundicon.png')} style={styles.audioIcon} />
        ) : (
          <Text key={index} style={styles.diaryContent}>{block.content}</Text>
        )
      ))}
      <Text>{item.content}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={diaryEntries}
        renderItem={renderDiaryEntry}
        keyExtractor={item => item.id}
        style={styles.diaryList}
      />
      <TouchableOpacity style={styles.openModalButton} onPress={() => setModalVisible(true)}>
        <Text>일기 작성</Text>
      </TouchableOpacity>
      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={handleCloseModal}
      >
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{ flex: 1 }}
        >
          <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
            <View style={styles.modalBackground}>
              <TouchableWithoutFeedback onPress={() => {}}>
                <View style={styles.modalView} {...panResponder.panHandlers}>
                  <View style={styles.header}>
                    <TouchableOpacity onPress={handleCloseModal} style={styles.closeButton}>
                      <Ionicons name="close" size={30} color="black" />
                    </TouchableOpacity>
                  </View>
                  <TextInput
                    style={styles.input}
                    placeholder="제목을 입력하세요"
                    placeholderTextColor="#000" // 진한 색상으로 설정
                    value={title}
                    onChangeText={setTitle}
                  />
                  <RichToolbar
                    editor={richText}
                    actions={['bold', 'italic', 'underline', 'bullets', 'orderedList']}
                    iconTint="#000000"
                  />
                  <View style={styles.modalContent}>
                    <View style={styles.grid}>
                      {blocks.map((block, index) => (
                        <View style={styles.block} key={block.id}>
                          {block.type === 'image' && block.uri ? (
                            <Image source={{ uri: block.uri }} style={styles.image} />
                          ) : block.type === 'date' ? (
                            <Text style={styles.dateBlock}>{block.content}</Text>
                          ) : block.type === 'audio' ? (
                            <Image source={require('../../assets/soundicon.png')} style={styles.audioIcon} />
                          ) : (
                            <TextInput
                              style={styles.textInput}
                              placeholder="내용을 입력하세요..."
                              multiline
                              onChangeText={(text) => handleTextChange(block.id, text)}
                              value={block.content}
                            />
                          )}
                          <TouchableOpacity onPress={() => handleRemoveBlock(block.id)} style={styles.removeButton}>
                            <Ionicons name="close-circle" size={24} color="black" />
                          </TouchableOpacity>
                        </View>
                      ))}
                    </View>
                    <View style={styles.richEditorContainer}>
                      <RichEditor
                        ref={richText}
                        style={styles.richEditor}
                        placeholder="내용을 입력하세요..."
                        initialContentHTML={content}
                        scrollEnabled={true}
                        onBlur={Keyboard.dismiss} // 포커스를 잃을 때 키보드 닫기
                      />
                    </View>
                  </View>
                  <View style={styles.iconRow}>
                    <TouchableOpacity onPress={handleImagePicker}>
                      <Ionicons name="image" size={30} color="black" />
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => handleAddBlock('audio')}>
                      <Ionicons name="mic" size={30} color="black" />
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => setDatePickerVisible(true)}>
                      <Ionicons name="calendar" size={30} color="black" />
                    </TouchableOpacity>
                    <TouchableOpacity onPress={handleSave} style={styles.saveButton}>
                      <Text style={styles.saveButtonText}>Save</Text>
                    </TouchableOpacity>
                  </View>
                  {datePickerVisible && (
                    <DateTimePicker
                      value={selectedDate}
                      mode="date"
                      display="default"
                      onChange={handleDateChange}
                    />
                  )}
                </View>
              </TouchableWithoutFeedback>
            </View>
          </TouchableWithoutFeedback>
        </KeyboardAvoidingView>
        <Modal
          animationType="slide"
          transparent={true}
          visible={confirmVisible}
          onRequestClose={() => setConfirmVisible(false)}
        >
          <TouchableWithoutFeedback onPress={() => setConfirmVisible(false)}>
            <View style={styles.modalBackground}>
              <TouchableWithoutFeedback onPress={() => {}}>
                <View style={styles.confirmModalView}>
                  <Text style={styles.confirmText}>콘텐츠가 삭제됩니다.</Text>
                  <View style={styles.confirmButtonRow}>
                    <TouchableOpacity onPress={() => handleConfirmClose('delete')} style={styles.deleteButton}>
                      <Text style={styles.deleteButtonText}>삭제</Text>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => handleConfirmClose('save')} style={styles.saveTempButton}>
                      <Text style={styles.saveTempButtonText}>저장</Text>
                    </TouchableOpacity>
                  </View>
                </View>
              </TouchableWithoutFeedback>
            </View>
          </TouchableWithoutFeedback>
        </Modal>
      </Modal>
    </View>
  );
};

export default ManualWriteScreen;