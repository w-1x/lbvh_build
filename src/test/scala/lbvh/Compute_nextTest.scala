package lbvh

import config.Configs._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class Compute_nextTest extends AnyFlatSpec with ChiselScalatestTester {
  "Compute_nextTest" should "pass test" in {
    test(new Compute_next_begin_and_end)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.io.input.valid.poke(1)
        dut.io.input.begin.poke(7)
        dut.io.input.end.poke(15)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(7)
        dut.io.input.node.merge_index.poke(0)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(8)
        dut.io.input.node.merge_index.poke(1)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(9)
        dut.io.input.node.merge_index.poke(1)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(10)
        dut.io.input.node.merge_index.poke(1)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(11)
        dut.io.input.node.merge_index.poke(2)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(12)
        dut.io.input.node.merge_index.poke(2)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(13)
        dut.io.input.node.merge_index.poke(3)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(14)
        dut.io.input.node.merge_index.poke(3)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(15)
        dut.io.input.node.merge_index.poke(4)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(16)
        dut.clock.step(1)
        dut.io.input.node.node_id.poke(17)
        dut.clock.step(10)
      }
  }
}
