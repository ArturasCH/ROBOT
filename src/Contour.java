
import java.awt.Point;
import java.util.LinkedList;


public class Contour {
    LinkedList<Line> lines = new LinkedList();
    
    
    int size()
    {
         return lines.size();
    }
   
    void changeDirection()
    {
         LinkedList<Line> temp = new LinkedList();
        for(int i = lines.size()-1; i >= 0; i--)
        {
            Point tmp = lines.get(i).getEndingCoordinates();
            lines.get(i).setEndingCoordinates(lines.get(i).getStartingCoordinates());
            lines.get(i).setStartingCoordinates(tmp);
            temp.add(lines.get(i));        
        } 
        
        lines = temp;
    }
    
    void startFrom(int index)
    {
        LinkedList<Line> tmp = new LinkedList();
        for (int i = index; i < lines.size(); i++)
        {
            tmp.add(lines.get(i));
        }
        for(int i = 0; i < index; i++)
        {
            tmp.add(lines.get(i));
        }
        
        lines = tmp;
    }
    
    void add(Line line)
    {
        lines.add(line);
    }
    
    void remove(Line line)
    {
        lines.remove(line);
    }
    
    void remove(int index)
    {
        lines.remove(index);
    }
    
    
    
    Line get(int index)
    {
        return lines.get(index);
    }
    
    boolean contains(Line line)
    {
        return lines.contains(line);
    }
      boolean contains(Contour contour)
    {
        boolean found = false;
        FINDING:
      for(int i = 0; i < contour.size(); i++)
      {for(int j = 0; j < lines.size(); j++)
          if(contour.get(i).getStartingCoordinates().equals(lines.get(j).getStartingCoordinates()) || 
             contour.get(i).getStartingCoordinates().equals(lines.get(j).getEndingCoordinates()) ||
             contour.get(i).getEndingCoordinates().equals(lines.get(j).getStartingCoordinates()) ||
             contour.get(i).getEndingCoordinates().equals(lines.get(j).getEndingCoordinates()))
          {
              found = true;
              break FINDING;
          }
          
      }
        
        return found;
    }
      
      void merge(Contour contour)
      {
          lines.addAll(contour.lines);
      }
      
     Line beginContourFrom()
    {
        
        Line found = null;
       for(int i = 0; i < lines.size(); i++)
       {
            boolean convergesOnBeginning=false;
            boolean convergesOnEnding=false;
           for(int j = 0; j < lines.size(); j++ )
           {
               if(!lines.get(i).equals(lines.get(j))){
             if(lines.get(i).startingPointConnects(lines.get(j).getStartingCoordinates()))                 
             {
                 convergesOnBeginning = true;
             }
             if(lines.get(i).startingPointConnects(lines.get(j).getEndingCoordinates()))
             {
                 convergesOnBeginning = true;
             }
             if(lines.get(i).endingPointConnects(lines.get(j).getStartingCoordinates()))
             {
                 convergesOnEnding = true;                 
             }
             if(lines.get(i).endingPointConnects(lines.get(j).getEndingCoordinates()))
             {
                 convergesOnEnding = true;
             }
             
           }
               
         
           }
               if(convergesOnBeginning==true && convergesOnEnding==false)
           {             
              found = lines.get(i);
                 lines.remove(found);
                 found.changeDirection();
               break;
           }
             if(convergesOnBeginning==false && convergesOnEnding==true)
             {                                 
                  found =lines.get(i);
               lines.remove(found);
                 break;
             }
       }       
       
       if(found != null)
       return found;
       else 
       {
       found = lines.get(0);
       lines.remove(0);
       return found;
       }
    }
    
     boolean checkIfOpenContour()
     {
        if(lines.get(0).startingPointConnects(lines.get(lines.size()-1).getEndingCoordinates()))
        {
        return true;
        }
        else
        return false;
     }
     
    void printLines()
    {
        
        for (Line line : lines) {
            line.printCoordinates();
        }
        System.out.println("\n");
        
    }
}
