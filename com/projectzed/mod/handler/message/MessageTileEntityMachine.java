package com.projectzed.mod.handler.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

import com.projectzed.api.tileentity.machine.AbstractTileEntityMachine;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author hockeyhurd
 * @version Oct 23, 2014
 */
public class MessageTileEntityMachine implements IMessage, IMessageHandler<MessageTileEntityMachine, IMessage> {

	public AbstractTileEntityMachine te;
	public int x, y, z;
	public int stored;
	public boolean powerMode;
	
	public MessageTileEntityMachine() {
	}
	
	public MessageTileEntityMachine(AbstractTileEntityMachine te) {
		this.te = te;
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.stored = te.getEnergyStored();
		this.powerMode = te.isPoweredOn();
	}
	
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.stored = buf.readInt();
		this.powerMode = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(stored);
		buf.writeBoolean(powerMode);
	}

	public IMessage onMessage(MessageTileEntityMachine message, MessageContext ctx) {
		TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		
		if (te instanceof AbstractTileEntityMachine) {
			((AbstractTileEntityMachine) te).setEnergyStored(message.stored);
			((AbstractTileEntityMachine) te).setPowerMode(message.powerMode);
		}
		
		return null;
	}

}
