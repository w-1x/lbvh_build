package lbvh

import config.Configs._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class TopTest extends AnyFlatSpec with ChiselScalatestTester {
  "TOPTest" should "pass test" in {
    test(new TOP)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.io.input.valid.poke(1)
        dut.clock.step(130)
      }
  }
}
