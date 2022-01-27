one sig List {
  header: lone Node
}

sig Node {
  link: lone Node
}

pred Acyclic() {
  // Generating List.header.*link requires cost >= 6
  all n: Node | n in \E\ => n !in n.^link
}

