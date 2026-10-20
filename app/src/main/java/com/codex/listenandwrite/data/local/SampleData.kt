package com.codex.listenandwrite.data.local

import com.codex.listenandwrite.data.entity.DictationItemEntity
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.entity.WordBookItemEntity
import com.codex.listenandwrite.data.entity.WordEntity

object SampleData {

    suspend fun prepopulate(database: AppDatabase) {
        val textbookDao = database.textbookDao()
        if (textbookDao.getCount() > 0) return

        val unitDao = database.unitDao()
        val wordDao = database.wordDao()
        val mistakeDao = database.mistakeDao()
        val dictationDao = database.dictationDao()
        val wordBookDao = database.wordBookDao()

        // 1. Insert Textbooks
        val textbooks = listOf(
            TextbookEntity(
                id = "pep_3_1",
                title = "三年级 上册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 3,
                semester = 1,
                coverStyle = "orange",
                unitCount = 6,
                wordCount = 234,
                isSelected = false
            ),
            TextbookEntity(
                id = "pep_3_2",
                title = "三年级 下册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 3,
                semester = 2,
                coverStyle = "blue",
                unitCount = 6,
                wordCount = 218,
                isSelected = true // Selected by default as in reference UI
            ),
            TextbookEntity(
                id = "pep_4_1",
                title = "四年级 上册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 4,
                semester = 1,
                coverStyle = "green",
                unitCount = 6,
                wordCount = 261,
                isSelected = false
            ),
            TextbookEntity(
                id = "pep_4_2",
                title = "四年级 下册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 4,
                semester = 2,
                coverStyle = "yellow",
                unitCount = 6,
                wordCount = 245,
                isSelected = false
            ),
            TextbookEntity(
                id = "pep_5_1",
                title = "五年级 上册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 5,
                semester = 1,
                coverStyle = "orange",
                unitCount = 6,
                wordCount = 270,
                isSelected = false
            ),
            TextbookEntity(
                id = "pep_5_2",
                title = "五年级 下册",
                publisher = "人教版 PEP (三年级起点)",
                grade = 5,
                semester = 2,
                coverStyle = "purple",
                unitCount = 6,
                wordCount = 258,
                isSelected = false
            )
        )
        textbookDao.insertTextbooks(textbooks)

        // 2. Insert Units for "三年级 下册" (pep_3_2)
        val units = listOf(
            UnitEntity("u32_1", "pep_3_2", 1, "Unit 1 Welcome back to school", 34),
            UnitEntity("u32_2", "pep_3_2", 2, "Unit 2 My family", 32),
            UnitEntity("u32_3", "pep_3_2", 3, "Unit 3 At the zoo", 36),
            UnitEntity("u32_4", "pep_3_2", 4, "Unit 4 Where is my car?", 30),
            UnitEntity("u32_5", "pep_3_2", 5, "Unit 5 Do you like pears?", 32),
            UnitEntity("u32_6", "pep_3_2", 6, "Unit 6 How many?", 28)
        )
        unitDao.insertUnits(units)

        // 3. Insert detailed Words for PEP Grade 3 Semester 2
        val words = mutableListOf<WordEntity>()

        // Unit 1: Welcome back to school
        val u1Words = listOf(
            Triple("UK", "[ˌjuː ˈkeɪ]", "英国"),
            Triple("Canada", "[ˈkænədə]", "加拿大"),
            Triple("USA", "[ˌjuː es ˈeɪ]", "美国"),
            Triple("China", "[ˈtʃaɪnə]", "中国"),
            Triple("she", "[ʃiː]", "她"),
            Triple("student", "[ˈstjuːdnt]", "学生"),
            Triple("pupil", "[ˈpjuːpl]", "小学生"),
            Triple("he", "[hiː]", "他"),
            Triple("teacher", "[ˈtiːtʃə(r)]", "老师"),
            Triple("boy", "[bɔɪ]", "男孩"),
            Triple("and", "[ænd]", "和，与"),
            Triple("girl", "[ɡɜːl]", "女孩"),
            Triple("new", "[njuː]", "新的"),
            Triple("friend", "[frend]", "朋友"),
            Triple("today", "[təˈdeɪ]", "今天"),
            Triple("welcome", "[ˈwelkəm]", "欢迎"),
            Triple("back", "[bæk]", "回来")
        )
        u1Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u1_$i", "u32_1", w, p, t))
        }

        // Unit 2: My family
        val u2Words = listOf(
            Triple("father", "[ˈfɑːðə(r)]", "父亲，爸爸"),
            Triple("dad", "[dæd]", "爸爸（口语）"),
            Triple("man", "[mæn]", "男人"),
            Triple("woman", "[ˈwʊmən]", "女人"),
            Triple("mother", "[ˈmʌðə(r)]", "母亲，妈妈"),
            Triple("sister", "[ˈsɪstə(r)]", "姐，妹"),
            Triple("brother", "[ˈbrʌðə(r)]", "兄，弟"),
            Triple("grandmother", "[ˈɡrænmʌðə(r)]", "祖母，外祖母"),
            Triple("grandma", "[ˈɡrænmɑː]", "奶奶，外婆"),
            Triple("grandfather", "[ˈɡrænfɑːðə(r)]", "祖父，外祖父"),
            Triple("grandpa", "[ˈɡrænpɑː]", "爷爷，外公"),
            Triple("family", "[ˈfæməli]", "家庭，家人"),
            Triple("who", "[huː]", "谁"),
            Triple("really", "[ˈrɪəli]", "真地，确实")
        )
        u2Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u2_$i", "u32_2", w, p, t))
        }

        // Unit 3: At the zoo
        val u3Words = listOf(
            Triple("thin", "[θɪn]", "瘦的"),
            Triple("fat", "[fæt]", "胖的"),
            Triple("tall", "[tɔːl]", "高的"),
            Triple("short", "[ʃɔːt]", "矮的，短的"),
            Triple("long", "[lɒŋ]", "长的"),
            Triple("small", "[smɔːl]", "小的"),
            Triple("big", "[bɪɡ]", "大的"),
            Triple("giraffe", "[dʒəˈrɑːf]", "长颈鹿"),
            Triple("so", "[səʊ]", "这么，那么"),
            Triple("children", "[ˈtʃɪldrən]", "儿童"),
            Triple("tail", "[teɪl]", "尾巴"),
            Triple("zoo", "[zuː]", "动物园")
        )
        u3Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u3_$i", "u32_3", w, p, t))
        }

        // Unit 4: Where is my car?
        val u4Words = listOf(
            Triple("on", "[ɒn]", "在...上"),
            Triple("in", "[ɪn]", "在...里"),
            Triple("under", "[ˈʌndə(r)]", "在...下面"),
            Triple("chair", "[tʃeə(r)]", "椅子"),
            Triple("desk", "[desk]", "书桌"),
            Triple("cap", "[kæp]", "帽子"),
            Triple("ball", "[bɔːl]", "球"),
            Triple("car", "[kɑː(r)]", "小汽车"),
            Triple("boat", "[bəʊt]", "小船"),
            Triple("map", "[mæp]", "地图"),
            Triple("toy", "[tɔɪ]", "玩具"),
            Triple("box", "[bɒks]", "盒子")
        )
        u4Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u4_$i", "u32_4", w, p, t))
        }

        // Unit 5: Do you like pears?
        val u5Words = listOf(
            Triple("pear", "[peə(r)]", "梨"),
            Triple("apple", "[ˈæpl]", "苹果"),
            Triple("orange", "[ˈɒrɪndʒ]", "橙子"),
            Triple("banana", "[bəˈnɑːnə]", "香蕉"),
            Triple("watermelon", "[ˈwɔːtəmelən]", "西瓜"),
            Triple("strawberry", "[ˈstrɔːbəri]", "草莓"),
            Triple("grape", "[ɡreɪp]", "葡萄"),
            Triple("buy", "[baɪ]", "买"),
            Triple("fruit", "[fruːt]", "水果")
        )
        u5Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u5_$i", "u32_5", w, p, t))
        }

        // Unit 6: How many?
        val u6Words = listOf(
            Triple("eleven", "[ɪˈlevn]", "十一"),
            Triple("twelve", "[twelv]", "十二"),
            Triple("thirteen", "[ˌθɜːˈtiːn]", "十三"),
            Triple("fourteen", "[ˌfɔːˈtiːn]", "十四"),
            Triple("fifteen", "[ˌfɪfˈtiːn]", "十五"),
            Triple("sixteen", "[ˌsɪksˈtiːn]", "十六"),
            Triple("seventeen", "[ˌsevnˈtiːn]", "十七"),
            Triple("eighteen", "[ˌeɪˈtiːn]", "十八"),
            Triple("nineteen", "[ˌnaɪnˈtiːn]", "十九"),
            Triple("twenty", "[ˈtwenti]", "二十"),
            Triple("kite", "[kaɪt]", "风筝"),
            Triple("beautiful", "[ˈbjuːtɪfl]", "美丽的")
        )
        u6Words.forEachIndexed { i, (w, p, t) ->
            words.add(WordEntity("w_u6_$i", "u32_6", w, p, t))
        }

        wordDao.insertWords(words)

        // 4. Insert initial Mistake Records matching Reference UI (Screen 10)
        val initialMistakes = listOf(
            MistakeRecordEntity(
                word = "family",
                phonetic = "['fæməli]",
                translation = "家庭，家人",
                unitTitle = "Unit 2 My family",
                mistakeCount = 3,
                lastMistakeTime = System.currentTimeMillis() - 1000 * 60 * 30,
                isFavorite = true,
                isMastered = false
            ),
            MistakeRecordEntity(
                word = "zoo",
                phonetic = "[zuː]",
                translation = "[n.] 动物园",
                unitTitle = "Unit 3 At the zoo",
                mistakeCount = 2,
                lastMistakeTime = System.currentTimeMillis() - 1000 * 3600 * 4,
                isFavorite = true,
                isMastered = false
            ),
            MistakeRecordEntity(
                word = "pear",
                phonetic = "[peə(r)]",
                translation = "梨",
                unitTitle = "Unit 5 Do you like pears?",
                mistakeCount = 1,
                lastMistakeTime = System.currentTimeMillis() - 1000 * 3600 * 24,
                isFavorite = false,
                isMastered = false
            ),
            MistakeRecordEntity(
                word = "giraffe",
                phonetic = "[dʒəˈrɑːf]",
                translation = "长颈鹿",
                unitTitle = "Unit 3 At the zoo",
                mistakeCount = 2,
                lastMistakeTime = System.currentTimeMillis() - 1000 * 3600 * 48,
                isFavorite = true,
                isMastered = false
            )
        )
        initialMistakes.forEach { mistakeDao.insertOrUpdate(it) }

        // 5. Insert historical sessions matching Screen 11 reference
        val oneDayMillis = 24L * 3600 * 1000
        val now = System.currentTimeMillis()

        val historySessions = listOf(
            DictationSessionEntity(
                timestamp = now - 1000 * 3600 * 2, // Today 15:20
                textbookTitle = "三年级 下册",
                unitRange = "Unit1-3",
                mode = "INPUT",
                totalCount = 20,
                correctCount = 16,
                wrongCount = 4,
                accuracy = 0.85f,
                durationSeconds = 632
            ),
            DictationSessionEntity(
                timestamp = now - oneDayMillis, // Yesterday 17:10
                textbookTitle = "三年级 下册",
                unitRange = "Unit1-2",
                mode = "PAPER",
                totalCount = 20,
                correctCount = 18,
                wrongCount = 2,
                accuracy = 0.90f,
                durationSeconds = 580
            ),
            DictationSessionEntity(
                timestamp = now - 2 * oneDayMillis,
                textbookTitle = "三年级 下册",
                unitRange = "Unit 1",
                mode = "INPUT",
                totalCount = 15,
                correctCount = 12,
                wrongCount = 3,
                accuracy = 0.80f,
                durationSeconds = 420
            ),
            DictationSessionEntity(
                timestamp = now - 3 * oneDayMillis,
                textbookTitle = "三年级 下册",
                unitRange = "Unit 2",
                mode = "INPUT",
                totalCount = 20,
                correctCount = 14,
                wrongCount = 6,
                accuracy = 0.70f,
                durationSeconds = 590
            ),
            DictationSessionEntity(
                timestamp = now - 4 * oneDayMillis,
                textbookTitle = "三年级 下册",
                unitRange = "Unit 3",
                mode = "INPUT",
                totalCount = 20,
                correctCount = 15,
                wrongCount = 5,
                accuracy = 0.75f,
                durationSeconds = 610
            ),
            DictationSessionEntity(
                timestamp = now - 5 * oneDayMillis,
                textbookTitle = "三年级 上册",
                unitRange = "全部单元",
                mode = "INPUT",
                totalCount = 30,
                correctCount = 26,
                wrongCount = 4,
                accuracy = 0.867f,
                durationSeconds = 850
            ),
            DictationSessionEntity(
                timestamp = now - 6 * oneDayMillis,
                textbookTitle = "三年级 下册",
                unitRange = "Unit 1",
                mode = "PAPER",
                totalCount = 15,
                correctCount = 13,
                wrongCount = 2,
                accuracy = 0.867f,
                durationSeconds = 410
            )
        )
        historySessions.forEach { dictationDao.insertSession(it) }

        // 6. Insert Custom Word Book
        val customBook = WordBookEntity(
            id = "custom_daily_1",
            title = "每日高频核心词",
            description = "精选小学阶段最常考的 50 个高频核心动词与名词",
            wordCount = 12,
            isCustom = true
        )
        wordBookDao.insertWordBook(customBook)
        val customItems = listOf(
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "family", phonetic = "['fæməli]", translation = "家庭，家人"),
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "student", phonetic = "[ˈstjuːdnt]", translation = "学生"),
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "teacher", phonetic = "[ˈtiːtʃə(r)]", translation = "老师"),
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "friend", phonetic = "[frend]", translation = "朋友"),
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "giraffe", phonetic = "[dʒəˈrɑːf]", translation = "长颈鹿"),
            WordBookItemEntity(wordBookId = "custom_daily_1", word = "banana", phonetic = "[bəˈnɑːnə]", translation = "香蕉")
        )
        wordBookDao.insertWordBookItems(customItems)
    }
}
