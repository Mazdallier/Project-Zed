package com.projectzed.mod.handler.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.projectzed.api.tileentity.container.AbstractTileEntityContainer;
import com.projectzed.mod.tileentity.container.TileEntityEnergyBankBase;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;

/**
 * Class containing creation of message to be sent from either side.
 * 
 * @author hockeyhurd
 * @version Dec 4, 2014
 */
public class MessageTileEntityContainer implements IMessage, IMessageHandler<MessageTileEntityContainer, IMessage> {

	public AbstractTileEntityContainer te;
	public int x, y, z;
	public int stored;

	// Energy cell specific.
	public boolean isEnergyCell;
	public byte[] openSides = new byte[ForgeDirection.VALID_DIRECTIONS.length];

	public MessageTileEntityContainer() {
	}

	/**
	 * @param te = tileentity to get data from.
	 */
	public MessageTileEntityContainer(AbstractTileEntityContainer te) {
		this.te = te;
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.stored = te.getEnergyStored();

		if (te instanceof TileEntityEnergyBankBase) {
			isEnergyCell = true;
			TileEntityEnergyBankBase temp = (TileEntityEnergyBankBase) te;

			for (int i = 0; i < openSides.length; i++) {
				this.openSides[i] = temp.getSideValve(i);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cpw.mods.fml.common.network.simpleimpl.IMessage#fromBytes(io.netty.buffer.ByteBuf)
	 */
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.stored = buf.readInt();
		this.isEnergyCell = buf.readBoolean();

		if (isEnergyCell) {
			for (int i = 0; i < openSides.length; i++) {
				openSides[i] = buf.readByte();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cpw.mods.fml.common.network.simpleimpl.IMessage#toBytes(io.netty.buffer.ByteBuf)
	 */
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(stored);
		buf.writeBoolean(isEnergyCell);

		if (isEnergyCell) {
			for (byte b : openSides) {
				buf.writeByte(b);
			}
		}
	}
	
	@Override
	public IMessage onMessage(MessageTileEntityContainer message, MessageContext ctx) {
		
		if (ctx.side == Side.CLIENT) {
			TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

			if (te instanceof AbstractTileEntityContainer) {
				((AbstractTileEntityContainer) te).setEnergyStored(message.stored);
			}
		}
		
		else if (ctx.side == Side.SERVER && message.isEnergyCell) {
			TileEntity tileEntity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
			
			if (tileEntity != null && tileEntity instanceof TileEntityEnergyBankBase) {
				TileEntityEnergyBankBase te = (TileEntityEnergyBankBase) tileEntity;
				
				if (te != null) {
					for (int i = 0; i < message.openSides.length; i++) {
						te.setSideValve(ForgeDirection.VALID_DIRECTIONS[i], message.openSides[i]);
					}
				}
			}
		}

		return null;
	}
	
}
