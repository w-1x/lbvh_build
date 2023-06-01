package lbvh

import config.Configs._
import chisel3.util._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class Compute_need_and_mergeTest
    extends AnyFlatSpec
    with ChiselScalatestTester {
  "Compute_need_and_mergeTest" should "pass test" in {
    test(new Compute_need_and_merge)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.io.input.valid.poke(0)
        dut.clock.step(1)
        dut.io.input.valid.poke(1)
        dut.io.input.level.poke(4)
        dut.clock.step(1)
        dut.io.input.level.poke(6)
        dut.clock.step(1)
        dut.io.input.level.poke(5)
        dut.clock.step(1)
        dut.io.input.level.poke(1)
        dut.clock.step(1)
        dut.io.input.level.poke(4)
        dut.clock.step(1)
        dut.io.input.level.poke(2)
        dut.clock.step(1)
        dut.io.input.level.poke(5)
        dut.clock.step(1)
        dut.io.input.level.poke(0)
        dut.clock.step(6)

      }
  }
}
