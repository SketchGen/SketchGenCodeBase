sig Node {
  neighbors: set Node,
} 	

fact undirected {
  neighbors = ~neighbors
  no iden & neighbors	
}

pred graphIsConnected {
  all n1: Node | all n2 : Node | n1 != n2 => n1 in n2.^neighbors  }



pred test0 {
some disj Node0, Node1: Node {
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1
!graphIsConnected[]
}
}
run test0