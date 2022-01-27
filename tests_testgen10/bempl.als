sig Room {}

sig Employee {
  owns : set Key
}

sig Key {
  authorized: one Employee,
  opened_by: one Room
}

pred CanEnter(p: Employee, r:Room) {
	r in p.owns.opened_by
}

pred Test0{
	some disj Room0 : Room | some disj Employee0 : Employee | some disj Key0, Key1, Key2 : Key |  { 
		Room = Room0
		Employee = Employee0
		Key = Key0 + Key1 + Key2
		owns = Employee0->Key0 + Employee0->Key1
		authorized = Key0->Employee0 + Key1->Employee0 + Key2->Employee0
		opened_by = Key0->Room0 + Key1->Room0 + Key2->Room0
		CanEnter[Employee0,Room0] 
		} 
}

pred Test1{
	some disj Room0 : Room | some disj Employee0 : Employee | some disj Key0, Key1, Key2 : Key |  { 
		Room = Room0
		Employee = Employee0
		Key = Key0 + Key1 + Key2
		no owns
		authorized = Key0->Employee0 + Key1->Employee0 + Key2->Employee0
		opened_by = Key0->Room0 + Key1->Room0  + Key2->Room0
		!CanEnter[Employee0,Room0] 
		} 
}

pred Test2{
	some disj Room0, Room1 : Room | some disj Employee0 : Employee | some disj Key0, Key1, Key2 : Key |  { 
		Room = Room0 + Room1
		Employee = Employee0
		Key = Key0 + Key1 + Key2
		owns = Employee0->Key2
		authorized = Key0->Employee0 + Key1->Employee0 + Key2->Employee0
		opened_by = Key0->Room0 + Key1->Room0  + Key2->Room0
		!CanEnter[Employee0,Room1] 
		} 
}

pred Test3{
	some disj Room0, Room1 : Room | some disj Employee0, Employee1, Employee2 : Employee | some disj Key0, Key1, Key2 : Key |  { 
		Room = Room0 + Room1
		Employee = Employee0 + Employee1 + Employee2
		Key = Key0 + Key1 + Key2
		owns = Employee0->Key1 + Employee1->Key0 + Employee1->Key1 + Employee2->Key2
		authorized = Key0->Employee2 + Key1->Employee2 + Key2->Employee1
		opened_by = Key0->Room1 + Key1->Room1 + Key2->Room0 
		CanEnter[Employee2,Room0] 
		} 
}

pred Test4{
	 some disj Room0, Room1 : Room |  some disj Employee0, Employee1 : Employee |  some disj Key0 : Key |  {
		Room = Room0 + Room1
		Employee = Employee0 + Employee1
		Key = Key0
		owns = Employee0->Key0 + Employee1->Key0
		authorized = Key0->Employee1
		opened_by = Key0->Room1
		CanEnter[Employee1,Room1]
	}
}

run Test4 for 3


pred Test5{
	 some disj Room0, Room1, Room2 : Room |  some disj Employee0, Employee1 : Employee |  some disj Key0, Key1, Key2 : Key |  {
		Room = Room0 + Room1 + Room2
		Employee = Employee0 + Employee1
		Key = Key0 + Key1 + Key2
		owns = Employee1->Key0 + Employee1->Key1 + Employee1->Key2
		authorized = Key0->Employee0 + Key1->Employee0 + Key2->Employee0
		opened_by = Key0->Room2 + Key1->Room1 + Key2->Room0
		CanEnter[Employee1,Room2]
	}
}

run Test5 for 3

pred Test6{
	 some disj Room0, Room1 : Room |  some disj Employee0 : Employee |  {
		Room = Room0 + Room1
		Employee = Employee0
		no Key
		no owns
		no authorized
		no opened_by
		!CanEnter[Employee0,Room1]
	}
}

run Test6 for 3

pred Test7{
	 some disj Room0 : Room |  some disj Employee0, Employee1, Employee2 : Employee |  some disj Key0, Key1 : Key |  {
		Room = Room0
		Employee = Employee0 + Employee1 + Employee2
		Key = Key0 + Key1
		owns = Employee1->Key1 + Employee2->Key0
		authorized = Key0->Employee0 + Key1->Employee0
		opened_by = Key0->Room0 + Key1->Room0
		CanEnter[Employee2,Room0]
	}
}

run Test7 for 3

pred Test8{
	 some disj Room0 : Room |  some disj Employee0, Employee1, Employee2 : Employee |  some disj Key0, Key1 : Key |  {
		Room = Room0
		Employee = Employee0 + Employee1 + Employee2
		Key = Key0 + Key1
		owns = Employee0->Key1 + Employee1->Key0 + Employee2->Key0
		authorized = Key0->Employee2 + Key1->Employee0
		opened_by = Key0->Room0 + Key1->Room0
		CanEnter[Employee2,Room0]
	}
}

run Test8 for 3

pred Test9{
	 some disj Room0, Room1, Room2 : Room |  some disj Employee0, Employee1 : Employee |  some disj Key0, Key1, Key2 : Key |  {
		Room = Room0 + Room1 + Room2
		Employee = Employee0 + Employee1
		Key = Key0 + Key1 + Key2
		owns = Employee1->Key2
		authorized = Key0->Employee0 + Key1->Employee0 + Key2->Employee1
		opened_by = Key0->Room2 + Key1->Room1 + Key2->Room0
		!CanEnter[Employee1,Room2]
	}
}

run Test9 for 3
