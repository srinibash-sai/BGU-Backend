package com.anup.bgu.Noticeboard.repo;

import com.anup.bgu.Noticeboard.entities.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
