/*
 * Copyright (C) 2015-2015 L2J EventEngine
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.eventengine.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.eventengine.EventEngineManager;
import net.sf.eventengine.datatables.MessageData;
import net.sf.eventengine.holder.PlayerHolder;

import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExEventMatchMessage;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author fissban, Zephyr
 */
public class EventUtil
{
	private static final Set<Integer> TIME_LEFT_TO_ANNOUNCE = new HashSet<>();
	static
	{
		TIME_LEFT_TO_ANNOUNCE.add(1800);
		TIME_LEFT_TO_ANNOUNCE.add(1200);
		TIME_LEFT_TO_ANNOUNCE.add(600);
		TIME_LEFT_TO_ANNOUNCE.add(300);
		TIME_LEFT_TO_ANNOUNCE.add(240);
		TIME_LEFT_TO_ANNOUNCE.add(120);
		TIME_LEFT_TO_ANNOUNCE.add(60);
		TIME_LEFT_TO_ANNOUNCE.add(30);
		TIME_LEFT_TO_ANNOUNCE.add(10);
		TIME_LEFT_TO_ANNOUNCE.add(5);
		TIME_LEFT_TO_ANNOUNCE.add(4);
		TIME_LEFT_TO_ANNOUNCE.add(3);
		TIME_LEFT_TO_ANNOUNCE.add(2);
		TIME_LEFT_TO_ANNOUNCE.add(1);
	}
	
	/**
	 * Do an announce with formated time left
	 * @param time
	 * @param textId
	 * @param say2
	 * @param mapToReplace : for example, to change %event% with event name
	 * @param toAllPlayers
	 */
	public static void announceTimeLeft(int time, String textId, int say2, Map<String, String> mapToReplace, boolean toAllPlayers)
	{
		if (TIME_LEFT_TO_ANNOUNCE.contains(time))
		{
			String announce;
			String timeLeft;
			Collection<L2PcInstance> listPlayers;
			if (toAllPlayers)
			{
				listPlayers = L2World.getInstance().getPlayers();
			}
			else
			{
				listPlayers = EventEngineManager.getAllRegisteredPlayers();
			}
			
			for (L2PcInstance player : listPlayers)
			{
				if (time > 60)
				{
					timeLeft = (time / 60) + " " + MessageData.getMsgByLang(player, "time_minutes", false);
				}
				else
				{
					timeLeft = time + " " + MessageData.getMsgByLang(player, "time_seconds", false);
				}
				
				announce = MessageData.getMsgByLang(player, textId, true);
				
				// Replace %time% with timeLeft
				announce = announce.replace("%time%", timeLeft);
				
				// Replace other holders
				if (mapToReplace != null)
				{
					for (String key : mapToReplace.keySet())
					{
						announce = announce.replace(key, mapToReplace.get(key));
					}
				}
				
				player.sendPacket(new CreatureSay(0, say2, "", announce));
			}
		}
	}
	
	public static void announceTimeLeft(int time, String textId, int say2, boolean toAllPlayers)
	{
		announceTimeLeft(time, textId, say2, null, toAllPlayers);
	}
	
	/**
	 * Send a message to a player inside the event
	 * @param player
	 * @param text
	 */
	public static void sendEventMessage(PlayerHolder player, String text)
	{
		player.getPcInstance().sendPacket(new CreatureSay(0, Say2.PARTYROOM_COMMANDER, "", text));
	}
	
	/**
	 * Create an event match message. type
	 * @param type -> 0 - gm, 1 - finish, 2 - start, 3 - game over, 4 - 1, 5 - 2, 6 - 3, 7 - 4, 8 - 5
	 * @param msg ->
	 */
	public static void sendEventSpecialMessage(PlayerHolder player, int type, String msg)
	{
		player.getPcInstance().sendPacket(new ExEventMatchMessage(type, msg));
	}
	
	/**
	 * Send a screen message to player inside the event
	 * @param player
	 * @param text
	 */
	public static void sendEventScreenMessage(PlayerHolder player, String text)
	{
		player.getPcInstance().sendPacket(new ExShowScreenMessage(text, 2000));
	}
	
	/**
	 * Send a screen message to all players in the event
	 * @param player
	 * @param text
	 * @param time
	 */
	public static void sendEventScreenMessage(PlayerHolder player, String text, int time)
	{
		player.getPcInstance().sendPacket(new ExShowScreenMessage(text, time));
	}
	
	/**
	 * Send a message to all players in the event
	 * @param say2
	 * @param text
	 */
	public static void announceToAllPlayersInEvent(int say2, String text)
	{
		for (L2PcInstance player : EventEngineManager.getAllRegisteredPlayers())
		{
			player.sendPacket(new CreatureSay(0, say2, "", MessageData.getMsgByLang(player, text, true)));
		}
	}
	
	/**
	 * Send a message to all online players
	 * @param say2
	 * @param text
	 */
	public static void announceToAllPlayers(int say2, String text)
	{
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			player.sendPacket(new CreatureSay(0, say2, "", MessageData.getMsgByLang(player, text, true)));
		}
	}
	
	/**
	 * Send a message to all online players
	 * @param say2
	 * @param text
	 * @param replace
	 * @param textReplace
	 */
	public static void announceToAllPlayers(int say2, String text, String replace, String textReplace)
	{
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			player.sendPacket(new CreatureSay(0, say2, "", MessageData.getMsgByLang(player, text, true).replace(replace, textReplace)));
		}
	}
}
