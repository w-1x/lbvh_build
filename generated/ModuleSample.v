module ModuleSample(
  input        clock,
  input        reset,
  input  [3:0] io_in_a,
  output [3:0] io_out_b
);
  assign io_out_b = io_in_a; // @[ModuleSample.scala 9:12]
endmodule
