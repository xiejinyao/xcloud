package com.xjinyao.xcloud.socket.message.command;

import com.xjinyao.xcloud.socket.command.IWebsocketMessageCommand;
import com.xjinyao.xcloud.socket.command.WebsocketMessageCommand;
import com.xjinyao.xcloud.socket.enums.CommandEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author 谢进伟
 * @description 基础命令
 * @createDate 2020/6/27 18:57
 */
@Data
@ApiModel("基础命令")
class BaseCommand implements Serializable {

	/**
	 * 消息id
	 */
	@ApiModelProperty("消息id")
	@Setter(AccessLevel.PRIVATE)
	protected String msgId = UUID.randomUUID().toString();
	/**
	 * 命令
	 */
	@ApiModelProperty("命令")
	protected IWebsocketMessageCommand command;
	/**
	 * 命令携带的参数数据
	 */
	@ApiModelProperty("命令携带的参数数据")
	protected Object data;
	/**
	 * 命令备注
	 */
	@ApiModelProperty("命令备注")
	protected String remark;

	@Tolerate
	public BaseCommand() {

	}

	protected BaseCommand(BaseCommandBuilder<?, ?> b) {
		this.msgId = b.msgId;
		this.command = b.command;
		this.data = b.data;
		this.remark = b.remark;
	}

	public String getCommand() {
		return this.command != null ? this.command.getCommand() : null;
	}

	public String getCommandRemark() {
		return this.command != null ? this.command.getRemark() : null;
	}

	public void setCommand(String command) {
		this.command(command, null);
	}

	public void command(IWebsocketMessageCommand command) {
		this.command = command;
	}

	public void command(CommandEnum command) {
		this.command(command.getCommand(), command.getRemark());
	}

	public void command(String command, String remark) {
		this.command = WebsocketMessageCommand.builder()
				.command(command)
				.remark(remark)
				.build();
	}


	public static BaseCommandBuilder<?, ?> builder() {
		return new BaseCommandBuilderImpl();
	}

	private static final class BaseCommandBuilderImpl extends BaseCommandBuilder<BaseCommand, BaseCommandBuilderImpl> {
		private BaseCommandBuilderImpl() {
		}

		protected BaseCommandBuilderImpl self() {
			return this;
		}

		public BaseCommand build() {
			return new BaseCommand(this);
		}
	}

	public abstract static class BaseCommandBuilder<C extends BaseCommand, B extends BaseCommandBuilder<C, B>> {
		private String msgId;
		private IWebsocketMessageCommand command;
		private Object data;
		private String remark;

		public BaseCommandBuilder() {
		}

		protected abstract B self();

		public abstract C build();

		public B msgId(String msgId) {
			this.msgId = msgId;
			return this.self();
		}

		public B command(IWebsocketMessageCommand command) {
			this.command = command;
			return this.self();
		}

		public B command(CommandEnum command) {
			this.command = WebsocketMessageCommand.builder()
					.command(command.getCommand())
					.remark(command.getRemark())
					.build();
			return this.self();
		}

		public B data(Object data) {
			this.data = data;
			return this.self();
		}

		public B remark(String remark) {
			this.remark = remark;
			return this.self();
		}
	}
}
