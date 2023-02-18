package com.core.jpa.repository;

import com.core.jpa.entity.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface SongRepository extends CrudRepository<Song, Integer> {

    List<Song> findByNameIn(Collection<String> names);
}
