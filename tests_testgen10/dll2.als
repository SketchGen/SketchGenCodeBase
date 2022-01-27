pred test0 {
some disj DLL0: DLL {some disj Node0, Node1: Node {
DLL = DLL0
header = DLL0->Node1
Node = Node0 + Node1
no pre
nxt = Node1->Node0
elem = Node0->-8 + Node1->0
!Sorted[]
}}
}
run test0

pred test1 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-6 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test1

pred test2 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-4 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test2

pred test3 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->7 + Node1->3 + Node2->7
!Sorted[]
}}
}
run test3

pred test4 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->0 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test4

pred test5 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->7 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test5

pred test6 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-7 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test6

pred test7 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->6 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test7

pred test8 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-3 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test8

pred test9 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-1 + Node1->-7 + Node2->-8
Sorted[]
}}
}
run test9

pred test10 {
some disj DLL0: DLL {some disj Node0, Node1, Node2: Node {
DLL = DLL0
header = DLL0->Node2
Node = Node0 + Node1 + Node2
no pre
nxt = Node1->Node0 + Node2->Node1
elem = Node0->-7 + Node1->-8 + Node2->-7
!Sorted[]
}}
}
run test10

pred Test11{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node1->Node0
		nxt = Node0->Node0
		elem = Node0->4 + Node1->5
		Sorted[]
	}
}

run Test11 for 3

pred Test12{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node1->Node0
		nxt = Node1->Node0
		elem = Node0->4 + Node1->5
		!Sorted[]
	}
}

run Test12 for 3

pred Test13{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0 + Node1->Node0
		nxt = Node0->Node0
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test13 for 3
pred Test15{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		nxt = Node0->Node0 + Node1->Node1
		elem = Node0->5 + Node1->4
		no pre
		Sorted[]
	}
}

run Test15 for 3

pred Test16{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		elem = Node0->5 + Node1->4
		no nxt
		Sorted[]
	}
}

run Test16 for 3

pred Test17{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		elem = Node0->5 + Node1->4
		no nxt
		Sorted[]
	}
}

run Test17 for 3

pred Test18{
	 some disj DLL0 : DLL |  some disj Node0, Node1, Node2 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1 + Node2
		header = DLL0->Node1
		pre = Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->4 + Node2->5
		no nxt
		Sorted[]
	}
}

run Test18 for 3

pred Test19{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0
		elem = Node0->4 + Node1->5
		no nxt
		Sorted[]
	}
}

run Test19 for 3

pred Test20{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->5 + Node1->4
		no pre
		Sorted[]
	}
}

run Test20 for 3

pred Test21{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		nxt = Node0->Node0
		elem = Node0->4 + Node1->5
		no header
		no pre
		Sorted[]
	}
}

run Test21 for 3

pred Test22{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		elem = Node0->5 + Node1->4
		no nxt
		Sorted[]
	}
}

run Test22 for 3

pred Test23{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->5 + Node1->4
		no pre
		Sorted[]
	}
}

run Test23 for 3

pred Test24{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		nxt = Node0->Node0
		elem = Node0->5 + Node1->-8
		no header
		no pre
		Sorted[]
	}
}

run Test24 for 3

pred Test25{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		nxt = Node0->Node0
		elem = Node0->4 + Node1->5
		no header
		no pre
		Sorted[]
	}
}

run Test25 for 3

pred Test26{
	 some disj DLL0 : DLL |  some disj Node0, Node1, Node2 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1 + Node2
		header = DLL0->Node1
		pre = Node0->Node1 + Node1->Node2
		nxt = Node0->Node0
		elem = Node0->7 + Node1->4 + Node2->5
		Sorted[]
	}
}

run Test26 for 3

pred Test27{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		nxt = Node0->Node0 + Node1->Node1
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test27 for 3

pred Test28{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		nxt = Node0->Node0 + Node1->Node1
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test28 for 3

pred Test29{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->4 + Node1->5
		!Sorted[]
	}
}

run Test29 for 3

pred Test30{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test30 for 3

pred Test31{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0
		nxt = Node0->Node0
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test31 for 3

pred Test32{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->4 + Node1->5
		!Sorted[]
	}
}

run Test32 for 3

pred Test33{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1
		nxt = Node0->Node0
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test33 for 3

pred Test34{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0
		nxt = Node0->Node0 + Node1->Node0
		elem = Node0->4 + Node1->5
		!Sorted[]
	}
}

run Test34 for 3

pred Test35{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node1
		nxt = Node0->Node0 + Node1->Node1
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test35 for 3

pred Test36{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0
		nxt = Node0->Node0
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test36 for 3

pred Test37{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		elem = Node0->5 + Node1->4
		no nxt
		Sorted[]
	}
}

run Test37 for 3

pred Test38{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1 + Node1->Node0
		elem = Node0->5 + Node1->4
		no nxt
		Sorted[]
	}
}

run Test38 for 3

pred Test39{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node0 + Node1->Node1
		nxt = Node0->Node0 + Node1->Node1
		elem = Node0->4 + Node1->5
		Sorted[]
	}
}

run Test39 for 3

pred Test40{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node1->Node0
		nxt = Node1->Node1
		elem = Node0->5 + Node1->4
		Sorted[]
	}
}

run Test40 for 3

pred Test41{
	 some disj DLL0 : DLL |  some disj Node0, Node1 : Node |  {
		DLL = DLL0
		Node = Node0 + Node1
		header = DLL0->Node0
		pre = Node0->Node1
		elem = Node0->4 + Node1->5
		no nxt
		Sorted[]
	}
}

run Test41 for 3


