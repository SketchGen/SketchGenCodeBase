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
run Test0 for 3
