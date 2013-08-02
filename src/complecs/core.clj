(ns complecs.core
  (:use
   potemkin)
  (:require
   complecs.state-machine
   complecs.entity-component-system))

(import-vars
 
 [complecs.entity-component-system

  entity
  defcomponent
  defsystem
  defcomponentsystem
  make-ecs
  ]

 [complecs.state-machine
  
  init-state-machine
  ]

 [complecs.util

  row
  col
  t-get
  int-table
  ]
 )
 
