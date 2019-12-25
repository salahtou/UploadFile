package com.updw.UPDW.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.updw.UPDW.model.DBFile;


@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {


	DBFile findByFileName(String fileName);

}