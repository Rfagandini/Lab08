package it.polito.tdp.extflightdelays.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;


public class Model {

	ExtFlightDelaysDAO ext = new ExtFlightDelaysDAO();
	Graph<Airport, DefaultWeightedEdge> grafo;
	List<Flight> flights = new ArrayList<>(ext.loadAllFlights());
	List<Airport> Airports = new ArrayList<>(ext.loadAllAirports());
	List<Arco> Archi = new ArrayList<>(ext.getArco());
	Map<Integer,Airport> MapAirport = new HashMap<>();
	Map<String,Arco> MapRoutes = new HashMap<>();
	String aux;
	
	public Model(){
		
	}
	
	public void creaGrafo(int distanciarichiesta) {
		
		for(Airport a: Airports) {
			if(MapAirport.get(a.getId())==null) {
				MapAirport.put(a.getId(), a);
			}
		}
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, MapAirport.values());
		
		List<Arco> ArcosMejorados = new ArrayList<>(Archi);
		List<Arco> ArcosX = new ArrayList<>(Archi);
		//System.out.println(Archi.size());
		
		Iterator<Arco> itr = Archi.iterator(); 
		
		for(Flight f: flights) {
			String id1 = f.getOriginAirportId()+"-"+f.getDestinationAirportId();
			String id2 = f.getDestinationAirportId()+"-"+f.getOriginAirportId();
			
			if(MapRoutes.get(id1)==null&&MapRoutes.get(id2)==null) {
				MapRoutes.put(id1, new Arco(f.getOriginAirportId(),f.getDestinationAirportId(),f.getDistance()));
			}
			else if( MapRoutes.get(id1)!=null) {
				MapRoutes.get(id1).setDistanzaMedia(f.getDistance());
			}
			else if( MapRoutes.get(id2)!=null) {
				MapRoutes.get(id2).setDistanzaMedia(f.getDistance());
			}
		}
		
		for(Arco a: MapRoutes.values()) {
			if(a.getDistanzaMedia()>distanciarichiesta) {
				Graphs.addEdge(this.grafo, MapAirport.get(a.getOriginAirportId()),MapAirport.get(a.getDestinationAirportId()),a.getDistanzaMedia());
				aux += "\n"+MapAirport.get(a.getOriginAirportId()).getAirportName()+" "+MapAirport.get(a.getDestinationAirportId()).getAirportName()+" "+a.getDistanzaMedia();
			}
		}
		aux += "\n Il grafo contiene "+this.grafo.vertexSet().size()+" vertici e : "+this.grafo.edgeSet().size()+" edges" ;
	}
	
	public Airport GetTheAirport(int id) {
		for(Airport a: Airports) {
			if(a.getId()==id) {
				return a;
			}
		}
		return null;
	}
	public String Risultati() {
		
		return aux.trim();
	}
}
