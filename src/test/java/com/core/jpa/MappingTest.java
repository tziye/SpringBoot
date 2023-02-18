package com.core.jpa;

import com.core.ApplicationTest;
import com.core.jpa.entity.*;
import com.core.jpa.repository.SingerRepository;
import com.core.jpa.repository.SongRepository;
import com.dto.GenderEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
class MappingTest extends ApplicationTest {

    @Autowired
    SingerRepository singerRepository;
    @Autowired
    SongRepository songRepository;

    @BeforeEach
    void setup() {
        Style love = Style.builder().style(Style.StyleEnum.情歌).build();
        Style rap = Style.builder().style(Style.StyleEnum.饶舌).build();

        Song s1 = Song.builder().name("以父之名").score(10).time(new Date()).build();
        Song s2 = Song.builder().name("夜曲").score(9).time(new Date()).build();

        Nickname nickname = Nickname.builder().name("Jay").build();
        Location location = Location.builder().country("中国").city("台北").build();

        Singer singer = Singer.builder().name("周杰伦").gender(GenderEnum.BOY).age(42).location(location)
                .song(new ArrayList<>(Arrays.asList(s1, s2))).style(new ArrayList<>(Arrays.asList(rap, love)))
                .nickname(nickname).build();

        s1.setBelongSinger(singer);
        s2.setBelongSinger(singer);
        nickname.setBelongSinger(singer);

        singerRepository.save(singer);
    }

    @AfterEach
    void down() {
        Singer singer = singerRepository.findByName("周杰伦").get(0);
        singerRepository.delete(singer);
    }

    @Test
    void findSinger() {
        Singer result = singerRepository.findByName("周杰伦").get(0);
        log.info("result: {}", result);
    }

    @Test
    void findSong() {
        List<Song> result = songRepository.findByNameIn(Arrays.asList("以父之名", "夜曲"));
        result.forEach(s -> log.info("{}", s));
    }

}
