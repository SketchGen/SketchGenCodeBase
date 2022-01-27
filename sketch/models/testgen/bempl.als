sig Room {}

sig Employee {
  owns : set Key
}

sig Key {
  authorized: one Employee,
  opened_by: one Room
}

pred CanEnter(p: Employee, r:Room) {
	r in \E\
}
