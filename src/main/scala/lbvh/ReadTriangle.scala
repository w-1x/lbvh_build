package lbvh

import chisel3._
import chisel3.util.experimental._
import chisel3.util._
import config.Configs._

class ReadTrianglesIO extends Bundle {
  val input = new Bundle {
    val id = Input(UInt(ADDR_WIDTH.W))
  }

  val output = new Bundle {
    val triangle = Output(new Triangle)
  }
}

class ReadTriangles extends Module { // 单周期
  val io = IO(new ReadTrianglesIO)

  val memory = SyncReadMem(DEPTH * 9, UInt(DATA_WIDTH.W))

  loadMemoryFromFile(memory, InputFileName)

  io.output.triangle.id := io.input.id - 1.U // 存储模块有一个时延，所以输出地址比输入地址慢一拍
  io.output.triangle.point_0.x := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 0.U
  )
  io.output.triangle.point_0.y := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 1.U
  )
  io.output.triangle.point_0.z := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 2.U
  )
  io.output.triangle.point_1.x := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 3.U
  )
  io.output.triangle.point_1.y := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 4.U
  )
  io.output.triangle.point_1.z := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 5.U
  )
  io.output.triangle.point_2.x := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 6.U
  )
  io.output.triangle.point_2.y := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 7.U
  )
  io.output.triangle.point_2.z := memory.read(
    io.input.id * 9.U % (9.U * DEPTH.U) + 8.U
  )
}
