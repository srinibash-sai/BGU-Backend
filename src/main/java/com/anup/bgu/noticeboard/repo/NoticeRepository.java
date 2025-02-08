package com.anup.bgu.noticeboard.repo;

import com.anup.bgu.noticeboard.entities.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
