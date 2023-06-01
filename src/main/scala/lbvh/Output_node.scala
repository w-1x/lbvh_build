package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Output_nodeIO extends Bundle {
  val input = new Bundle {
    val parent_node = Input(new Out_Node)
    val first_node = Input(new Node)
    val second_node = Input(new Node)
    val valid = Input(Bool())
  }

  val output1 = new Bundle {
    val parent_node = Output(new Out_Node)
    val valid = Output(Bool())
  }
  val output2 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
  val output3 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
  val output4 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
}

class Output_node extends Module { // 一个寄存器，1个周期
  val io = IO(new Output_nodeIO)

  val valid_dataReg = RegInit(false.B)
  val temp_parentnodeReg = Reg(new Out_Node)

  temp_parentnodeReg := io.input.parent_node
  io.output1.parent_node := temp_parentnodeReg

  when(io.input.parent_node.node.node_id =/= temp_parentnodeReg.node.node_id) {
    valid_dataReg := true.B
  }.otherwise {
    valid_dataReg := false.B
  }
  when(valid_dataReg) {
    io.output1.valid := true.B
  }.otherwise {
    io.output1.valid := false.B
  }

  io.output2.node := io.input.first_node
  when(io.input.first_node.node_id =/= Integer.MAX_VALUE.U) {
    io.output2.valid := true.B
  }.otherwise {
    io.output2.valid := false.B
  }

  io.output3.node := io.input.second_node
  when(io.input.second_node.node_id =/= Integer.MAX_VALUE.U) {
    io.output3.valid := true.B
  }.otherwise {
    io.output3.valid := false.B
  }

  io.output4.node := io.input.parent_node.node
  when(io.input.parent_node.next_end === 1.U && io.input.valid) {
    io.output4.valid := true.B
  }.otherwise {
    io.output4.valid := false.B
  }

}
