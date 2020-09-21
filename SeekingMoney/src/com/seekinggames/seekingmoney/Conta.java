package com.seekinggames.seekingmoney;

public class Conta {
	
	private String nome;
	private double money;
	
	public Conta(String nome){
		this.nome = nome;
	}
	
	public Conta(String nome, double money){
		this.nome = nome;
		this.money = money;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public double getMoney(){
		return money;
	}
	
	public void setMoney(double money){
		this.money = money;
	}

}
