package com.seekinggames.seekingmoney;

public class Contas {
	
	public static void criarConta(String conta){
		SeekingMoney.getSeekingMoney().getPlayers().add(new Conta(conta));
	}
	
	public static void criarConta(String conta, double quantidade){
		SeekingMoney.getSeekingMoney().getPlayers().add(new Conta(conta, quantidade));
	}
	
	public static Conta getConta(String conta){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)) return contas;
		}
		return null;
	}
	
	public static double getMoneyConta(String conta){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)) return contas.getMoney();
		}
		return 0.0;
	}
	
	public static boolean hasMoney(String conta, double quantidade){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta) && contas.getMoney() >= quantidade) return true;
		}
		return false;
	}
	
	public static boolean hasConta(String conta){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)) return true;
		}
		return false;
	}
	
	public static void tirarMoney(String conta, double quantidade){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)){
				contas.setMoney(contas.getMoney() - quantidade);
				if(contas.getMoney() < 0){
					contas.setMoney(0.0);
				}
				break;
			}
		}
	}
	
	public static void darMoney(String conta, double quantidade){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)){
				contas.setMoney(contas.getMoney() + quantidade);
				break;
			}
		}
	}
	
	public static void trocarMoney(String player1, String player2, double quantidade){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(player1)){
				for(Conta conta : SeekingMoney.getSeekingMoney().getPlayers()){
					if(conta.getNome().equalsIgnoreCase(player2)){
						contas.setMoney(contas.getMoney() - quantidade);
						conta.setMoney(conta.getMoney() + quantidade);
						break;
					}
				}
				break;
			}
		}
	}
	
	public static void setMoney(String conta, double quantidade){
		for(Conta contas : SeekingMoney.getSeekingMoney().getPlayers()){
			if(contas.getNome().equalsIgnoreCase(conta)){
				contas.setMoney(quantidade < 0 ? 0 : quantidade);
				break;
			}
		}
	}

}
