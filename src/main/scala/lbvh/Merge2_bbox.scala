package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Merge2_bbox extends Module { // 组合逻辑
  val io = IO(new Bundle {
    val input1_bbox = Input(new BoundingBox)
    val input2_bbox = Input(new BoundingBox)

    val output_bbox = Output(new BoundingBox)
  })
  val bbox_xmax_cmp_le = Module(new Float_CMP_le)
  val bbox_ymax_cmp_le = Module(new Float_CMP_le)
  val bbox_zmax_cmp_le = Module(new Float_CMP_le)

  val bbox_xmin_cmp_le = Module(new Float_CMP_le)
  val bbox_ymin_cmp_le = Module(new Float_CMP_le)
  val bbox_zmin_cmp_le = Module(new Float_CMP_le)
//求maxPoint
  bbox_xmax_cmp_le.io.a := io.input1_bbox.maxPoint.x
  bbox_xmax_cmp_le.io.b := io.input2_bbox.maxPoint.x

  bbox_ymax_cmp_le.io.a := io.input1_bbox.maxPoint.y
  bbox_ymax_cmp_le.io.b := io.input2_bbox.maxPoint.y

  bbox_zmax_cmp_le.io.a := io.input1_bbox.maxPoint.z
  bbox_zmax_cmp_le.io.b := io.input2_bbox.maxPoint.z

  when(bbox_xmax_cmp_le.io.out_bool) {
    io.output_bbox.maxPoint.x := bbox_xmax_cmp_le.io.b
  }.otherwise {
    io.output_bbox.maxPoint.x := bbox_xmax_cmp_le.io.a
  }
  when(bbox_ymax_cmp_le.io.out_bool) {
    io.output_bbox.maxPoint.y := bbox_ymax_cmp_le.io.b
  }.otherwise {
    io.output_bbox.maxPoint.y := bbox_ymax_cmp_le.io.a
  }
  when(bbox_zmax_cmp_le.io.out_bool) {
    io.output_bbox.maxPoint.z := bbox_zmax_cmp_le.io.b
  }.otherwise {
    io.output_bbox.maxPoint.z := bbox_zmax_cmp_le.io.a
  }
//求minPoint
  bbox_xmin_cmp_le.io.a := io.input1_bbox.minPoint.x
  bbox_xmin_cmp_le.io.b := io.input2_bbox.minPoint.x

  bbox_ymin_cmp_le.io.a := io.input1_bbox.minPoint.y
  bbox_ymin_cmp_le.io.b := io.input2_bbox.minPoint.y

  bbox_zmin_cmp_le.io.a := io.input1_bbox.minPoint.z
  bbox_zmin_cmp_le.io.b := io.input2_bbox.minPoint.z

  when(bbox_xmin_cmp_le.io.out_bool) {
    io.output_bbox.minPoint.x := bbox_xmin_cmp_le.io.a
  }.otherwise {
    io.output_bbox.minPoint.x := bbox_xmin_cmp_le.io.b
  }
  when(bbox_ymin_cmp_le.io.out_bool) {
    io.output_bbox.minPoint.y := bbox_ymin_cmp_le.io.a
  }.otherwise {
    io.output_bbox.minPoint.y := bbox_ymin_cmp_le.io.b
  }
  when(bbox_zmin_cmp_le.io.out_bool) {
    io.output_bbox.minPoint.z := bbox_zmin_cmp_le.io.a
  }.otherwise {
    io.output_bbox.minPoint.z := bbox_zmin_cmp_le.io.b
  }

}
