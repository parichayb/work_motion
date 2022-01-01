package com.example.workmotion.domain;

public enum EmployeeEvent {

  ADD {
    @Override
    public EmployeeEvent nextEvent() {
      return CHECK;
    }

    @Override
    public EmployeeState fetchState() {
      return EmployeeState.ADDED;
    }
  },
  CHECK {
    @Override
    public EmployeeEvent nextEvent() {
      return APPROVE;
    }

    @Override
    public EmployeeState fetchState() {
      return EmployeeState.IN_CHECK;
    }
  },
  APPROVE {
    @Override
    public EmployeeEvent nextEvent() {
      return ACTIVATE;
    }

    @Override
    public EmployeeState fetchState() {
      return EmployeeState.APPROVED;
    }
  },
  ACTIVATE {
    @Override
    public EmployeeEvent nextEvent() {
      return this;
    }

    @Override
    public EmployeeState fetchState() {
      return EmployeeState.ACTIVE;
    }
  };

  public abstract EmployeeEvent nextEvent();

  public abstract EmployeeState fetchState();

}
