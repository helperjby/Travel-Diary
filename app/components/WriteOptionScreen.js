import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const WriteOptionScreen = ({ navigation }) => {
    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.headerTitle}>글쓰기</Text>
                <Ionicons name="notifications-outline" size={24} color="black" />
            </View>
            <View style={styles.optionsContainer}>
                <TouchableOpacity style={styles.option} onPress={() => navigation.navigate('AIWrite')}>
                    <Text style={styles.optionTitle}>AI로 작성하기</Text>
                    <Text style={styles.optionDescription}>몇 가지 질문들을 통해 AI가 글을 더 풍성하게 만들어줍니다.</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.option} onPress={() => navigation.navigate('ManualWrite')}>
                    <Text style={styles.optionTitle}>직접 작성하기</Text>
                    <Text style={styles.optionDescription}>다이어리를 자유롭게 작성해보세요</Text>
                </TouchableOpacity>
            </View>
            <TouchableOpacity style={styles.selectButton}>
                <Text style={styles.selectButtonText}>선택</Text>
            </TouchableOpacity>
        </View>
    );
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
    },
    headerTitle: {
        fontSize: 24,
        fontWeight: 'bold',
    },
    optionsContainer: {
        flex: 1,
        justifyContent: 'center',
        paddingHorizontal: 20,
    },
    option: {
        padding: 20,
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 10,
        marginBottom: 20,
    },
    optionTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 10,
    },
    optionDescription: {
        fontSize: 14,
        color: 'gray',
    },
    selectButton: {
        backgroundColor: '#4285F4',
        padding: 15,
        alignItems: 'center',
        justifyContent: 'center',
        margin: 20,
        borderRadius: 5,
    },
    selectButtonText: {
        color: '#fff',
        fontSize: 16,
    },
});

export default WriteOptionScreen;
