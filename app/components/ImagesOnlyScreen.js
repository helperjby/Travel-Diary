import React, { useState } from 'react';
import { View, Image, StyleSheet, FlatList, Modal, TouchableOpacity, Text } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import ImageViewer from 'react-native-image-zoom-viewer';

const ImagesOnlyScreen = ({ route }) => {
  const { images } = route.params;
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

  const openModal = (index) => {
    setSelectedImageIndex(index);
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
  };

  const imageUrls = images.map(image => ({ url: '', props: { source: image } }));

  const renderItem = ({ item, index }) => (
    <TouchableOpacity onPress={() => openModal(index)} style={styles.imageContainer}>
      <Image source={item} style={styles.image} />
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={images}
        renderItem={renderItem}
        keyExtractor={(item, index) => index.toString()}
        numColumns={3}
      />
      {modalVisible && (
        <Modal
          visible={modalVisible}
          transparent={true}
          onRequestClose={closeModal}
        >
          <ImageViewer
            imageUrls={imageUrls}
            index={selectedImageIndex}
            onSwipeDown={closeModal}
            enableSwipeDown={true}
            renderHeader={() => (
              <TouchableOpacity style={styles.closeButton} onPress={closeModal}>
                <Ionicons name="close" size={30} color="white" />
              </TouchableOpacity>
            )}
            renderIndicator={(currentIndex, allSize) => (
              <View style={styles.indicatorContainer}>
                <Text style={styles.indicatorText}>{`${currentIndex} / ${allSize}`}</Text>
              </View>
            )}
          />
        </Modal>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  imageContainer: {
    flex: 1 / 3,
    aspectRatio: 1,
    margin: 1,
  },
  image: {
    width: '100%',
    height: '100%',
    borderRadius: 10,
  },
  closeButton: {
    position: 'absolute',
    top: 40,
    right: 20,
    zIndex: 1,
  },
  indicatorContainer: {
    position: 'absolute',
    bottom: 20,
    alignSelf: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 10,
  },
  indicatorText: {
    color: 'white',
    fontSize: 16,
  },
});

export default ImagesOnlyScreen;