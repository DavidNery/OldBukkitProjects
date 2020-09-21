package me.zfork.craftzone.mobspawn;

import me.zfork.craftzone.mobspawn.utils.MobSpawner;
import me.zfork.craftzone.mobspawn.utils.MobSpawnerUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor{
	
	private MobSpawn instance = MobSpawn.getMobSpawn();
	private MobSpawnerUtils mobspawnerutils = instance.getMobSpawnerUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("setmob") && sender instanceof Player){
			Player p = (Player) sender;
			if(!p.hasPermission("mobspawn.admin")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
				return true;
			}
			// /mob setar/remover <nome> <tipo> <tempo> <qnt> <raio>
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§"));
				return true;
			}else if(args[0].equalsIgnoreCase("setar")){
				if(args.length <= 5){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto_Setar").replace("&", "§"));
					return true;
				}
				try{
					String nome = args[1];
					EntityType type = EntityType.fromName(args[2]);
					int tempo = Integer.parseInt(args[3]);
					int quantidade = Integer.parseInt(args[4]);
					int raio = Integer.parseInt(args[5]);
					if(type == null){
						String mobs = "";
						for(EntityType s : EntityType.values()){
							if(s.getEntityClass() != null && 
									(Monster.class.isAssignableFrom(s.getEntityClass()) || Animals.class.isAssignableFrom(s.getEntityClass())))
								mobs += s.name().toLowerCase() + ", ";
						}
						for(String msg : instance.getConfig().getStringList("Mensagem.Erro.Mob_Invalido"))
							p.sendMessage(msg.replace("{mobs}", mobs.substring(0, mobs.length()-2)).replace("&", "§"));
						return true;
					}else if(tempo <= 0 || quantidade <= 0 || raio <= 0){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Valor_Maior_Que_Zero").replace("&", "§"));
						return true;
					}else if(mobspawnerutils.hasMobSpawner(nome)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Mob_Spawner").replace("&", "§"));
						return true;
					}
					mobspawnerutils.addMobSpawner(nome, p.getLocation(), quantidade, tempo, type, raio);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mob_Spawner_Setado").replace("&", "§"));
				}catch(NumberFormatException e){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
					return true;
				}
			}else if(args[0].equalsIgnoreCase("deletar")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto_Remover").replace("&", "§"));
					return true;
				}else if(!mobspawnerutils.hasMobSpawner(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Existe_Mob_Spawner").replace("&", "§"));
					return true;
				}
				mobspawnerutils.delMobSpawner(args[1]);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mob_Spawner_Deletado").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("listar")){
				if(mobspawnerutils.getMobspawners().size() == 0){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Mob_Spawners").replace("&", "§"));
					return true;
				}
				String spawners = "";
				for(MobSpawner ms : mobspawnerutils.getMobspawners())
					spawners += ms.getMobSpawnerName() + ", ";
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Spawners").replace("&", "§")
						.replace("{spawners}", spawners.substring(0, spawners.length()-2)));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§"));
				return true;
			}
		}
		return false;
	}

}
