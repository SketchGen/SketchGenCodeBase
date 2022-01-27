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