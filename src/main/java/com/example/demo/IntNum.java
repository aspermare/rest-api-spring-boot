package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="int_num")
public class IntNum {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	private Integer number = 0;
	
	protected IntNum() {}

	public IntNum(Integer number) {
		this.number = number;
    }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
        this.number = number;
	}
}