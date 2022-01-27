one sig List {
  header: lone Node
}

sig Node {
  link: lone Node
}

pred Acyclic {
	all n : Node | n in List.header.*link => n !in n.^link
}

pred Test0{
	some List0: List { 
		List = List0 
		no Node 
		no header
		no link
		Acyclic[]
	}
}

pred Test1{
	some List0: List | some Node0: Node { 
		List = List0 
		Node = Node0 
		header = List0 -> Node0 
		link = Node0 -> Node0
		!Acyclic[]
	}
}

pred Test2{
	some List0: List | some disj Node0, Node1 : Node { 
		List = List0 
		Node = Node0 + Node1 
		header = List0 -> Node0 
		link = Node0 -> Node1 + Node1 -> Node0 
		!Acyclic[]
	}
}

pred Test3{
	some List0: List | some disj Node0, Node1, Node2 : Node { 
		List = List0 
		Node = Node0 + Node1 + Node2 
		header = List0 -> Node0 
		link = Node0 -> Node1 + Node2 -> Node2 
		Acyclic[]
	}
}

pred Test4{
	some List0: List | some disj Node0, Node1, Node2: Node { 
		List = List0 
		Node = Node0 + Node1 + Node2 
		header = List0 -> Node0 
		link = Node0 -> Node1 + Node1 -> Node2 + Node2 -> Node2 
		!Acyclic[]
	}
}

pred Test6{
	 some disj List0 : List |  some disj Node0, Node1 : Node |  {
		List = List0
		Node = Node0 + Node1
		link = Node0->Node0
		no header
		Acyclic[]
	}
}

run Test6 for 3

pred Test7{
	 some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  {
		List = List0
		Node = Node0 + Node1 + Node2
		header = List0->Node1
		link = Node0->Node1 + Node1->Node2 + Node2->Node0
		!Acyclic[]
	}
}

run Test7 for 3

pred Test8{
	 some disj List0 : List |  some disj Node0, Node1 : Node |  {
		List = List0
		Node = Node0 + Node1
		header = List0->Node0
		link = Node0->Node0
		!Acyclic[]
	}
}

run Test8 for 3

pred Test9{
	 some disj List0 : List |  some disj Node0 : Node |  {
		List = List0
		Node = Node0
		header = List0->Node0
		no link
		Acyclic[]
	}
}

run Test9 for 3

pred Test10{
	 some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  {
		List = List0
		Node = Node0 + Node1 + Node2
		header = List0->Node1
		link = Node0->Node1 + Node1->Node2 + Node2->Node1
		!Acyclic[]
	}
}