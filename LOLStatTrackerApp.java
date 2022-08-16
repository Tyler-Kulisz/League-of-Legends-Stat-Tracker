package codejava.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.SwingConstants;

public class LOLStatTrackerApp extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane, contentPane2;
	private JTextField searchPlayer;
	private String regionSelected;
	private String apiKey;
	private HashMap<String,String> regions;
	private ArrayList<Object> soloDuo, flex;
	private JFrame rtFrame;

	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					LOLStatTrackerApp frame = new LOLStatTrackerApp();
					frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}	
			}
		});
	}

	//Builds the first page
	public void prepareGUI()
	{
		setFont(new Font("Airstrike", Font.BOLD, 12));
		setTitle("League of Legends Stat Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 549, 412);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setBackground(new Color(102, 153, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel programTitle = new JLabel("League Stat Tracker");
		programTitle.setForeground(Color.BLACK);
		programTitle.setFont(new Font("Airstrike", Font.BOLD, 30));
		programTitle.setBounds(68, 25, 397, 27);
		contentPane.add(programTitle);
		
		JLabel searchPlayerLabel = new JLabel("Search Player:");
		searchPlayerLabel.setForeground(Color.BLACK);
		searchPlayerLabel.setFont(new Font("Airstrike", Font.PLAIN, 18));
		searchPlayerLabel.setBounds(10, 237, 167, 20);
		contentPane.add(searchPlayerLabel);
		
		searchPlayer = new JTextField();
		searchPlayer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		searchPlayer.setForeground(Color.BLACK);
		searchPlayer.setBounds(174, 233, 185, 27);
		contentPane.add(searchPlayer);
		searchPlayer.setColumns(10);
		
		JLabel lblSlctReg = new JLabel("Select Region:");
		lblSlctReg.setForeground(Color.BLACK);
		lblSlctReg.setFont(new Font("Airstrike", Font.PLAIN, 21));
		lblSlctReg.setBounds(172, 80, 175, 14);
		contentPane.add(lblSlctReg);
		prepareList();
	}
		
	//Builds the list
	public void prepareList()
	{
		JList<String> list = new JList<String>();
		list.setForeground(Color.BLACK);
		list.setBackground(new Color(102, 153, 204));
		list.setFont(new Font("Airstrike", Font.PLAIN, 15));
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"North America", "Oceania", "Korea", "Europe Nordic & East", "Europe West", 
					"Latin America North", "Turkey", "Latin America South", "Japan", "Russia", "Brazil"};
			public int getSize() 
			{
				return values.length;
			}
			public String getElementAt(int index) 
			{
				return values[index];
			}
		});
		list.addListSelectionListener(new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent e) 
			{
				regionSelected = regions.get(list.getSelectedValue());
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBounds(100, 110, 366, 94);
		contentPane.add(list);
		searchButton();	
	}
	
	//Builds the Search Button and retrieves info from the API
	public void searchButton()
	{
		JLabel lblError = new JLabel("NOT FOUND");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Airstrike", Font.PLAIN, 20));
		lblError.setBounds(369, 237, 123, 17);
		contentPane.add(lblError);
		lblError.setVisible(false);
		
		JLabel lblError1 = new JLabel("NO RANKED STATS");
		lblError1.setForeground(Color.RED);
		lblError1.setFont(new Font("Airstrike", Font.PLAIN, 15));
		lblError1.setBounds(369, 237, 154, 17);
		contentPane.add(lblError1);
		lblError1.setVisible(false);
		
		JLabel lblError2 = new JLabel("MAKE A SELECTION");
		lblError2.setForeground(Color.RED);
		lblError2.setFont(new Font("Airstrike", Font.PLAIN, 15));
		lblError2.setBounds(369, 237, 154, 17);
		contentPane.add(lblError2);
		lblError2.setVisible(false);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt1)
			{
				lblError.setVisible(false);
				lblError1.setVisible(false);
				lblError2.setVisible(false);
				
				String playerName = searchPlayer.getText();
				playerName = playerName.replace(" ", "%20");
				try 
				{
					//GET request the SumonerV4 API
					URL url1 = new URL("https://"+ regionSelected + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + 
					playerName + "?api_key=" + apiKey);
					
					HttpURLConnection connection;
					try 
					{
						connection = (HttpURLConnection)url1.openConnection();
						connection.setRequestMethod("GET");
						connection.connect();
						int code = connection.getResponseCode();
						
						if(code != 200)
							lblError.setVisible(true);
						else
						{
							String inline = "";
							Scanner scan = new Scanner(url1.openStream());
							while(scan.hasNext())
							{
								inline+= scan.nextLine();
							}
							scan.close();
							connection.disconnect();
							
							//Parse into object as it only has 1 object
							JSONParser parse = new JSONParser();
							JSONObject data_obj;
							try 
							{
								data_obj = (JSONObject) parse.parse(inline);
								String playerID = (String) data_obj.get("id");
				
								//GET request the LeagueV4 API
								URL url2 = new URL("https://" + regionSelected + ".api.riotgames.com/lol/league/v4/entries/by-summoner/" 
								+ playerID + "?api_key=" + apiKey);
								
								HttpURLConnection connection2;
								try 
								{
									connection2 = (HttpURLConnection)url2.openConnection();
									connection2.setRequestMethod("GET");
									connection2.connect();
									int code2 = connection2.getResponseCode();
									if(code2 != 200)
									{
										throw new RuntimeException("HttpResponse: " + code2);
									}
									else
									{
										String inline2 = "";
										Scanner scan2 = new Scanner(url2.openStream());
										while(scan2.hasNext())
										{
											inline2+= scan2.nextLine();
										}
										scan2.close();
										connection2.disconnect();
										
										//Parse into an Array bkz there is possible Solo and Flex Stats
										JSONParser parse2 = new JSONParser();
										JSONArray data_array1;
										
										try
										{
											data_array1 = (JSONArray) parse2.parse(inline2);
											if(data_array1.size() == 0)
												lblError1.setVisible(true);
											else if(data_array1.size() == 1)
											{
												JSONObject temp = (JSONObject) data_array1.get(0);
												if(temp.get("queueType").equals("RANKED_SOLO_5x5"))
												{
													soloDuo.add(temp.get("queueType"));
													soloDuo.add(temp.get("tier"));
													soloDuo.add(temp.get("rank"));
													soloDuo.add(temp.get("leaguePoints"));
													soloDuo.add(temp.get("wins"));
													soloDuo.add(temp.get("losses"));
													soloDuo.add(temp.get("hotStreak"));
													for(int i = 0; i < 7; i++)
													{
														flex.add("NA");
													}
												}
												else
												{
													flex.add(temp.get("queueType"));
													flex.add(temp.get("tier"));
													flex.add(temp.get("rank"));
													flex.add(temp.get("leaguePoints"));
													flex.add(temp.get("wins"));
													flex.add(temp.get("losses"));
													flex.add(temp.get("hotStreak"));
													for(int i = 0; i < 7; i++)
													{
														soloDuo.add("NA");
													}
												}
												statsScreen();
											}
											else
											{
												for(int i = 0; i < data_array1.size(); i++)
												{
													JSONObject temp = (JSONObject) data_array1.get(i);
													if(temp.get("queueType").equals("RANKED_SOLO_5x5"))
													{
														soloDuo.add(temp.get("queueType"));
														soloDuo.add(temp.get("tier"));
														soloDuo.add(temp.get("rank"));
														soloDuo.add(temp.get("leaguePoints"));
														soloDuo.add(temp.get("wins"));
														soloDuo.add(temp.get("losses"));
														soloDuo.add(temp.get("hotStreak"));
													}
													else
													{
														flex.add(temp.get("queueType"));
														flex.add(temp.get("tier"));
														flex.add(temp.get("rank"));
														flex.add(temp.get("leaguePoints"));
														flex.add(temp.get("wins"));
														flex.add(temp.get("losses"));
														flex.add(temp.get("hotStreak"));
													}
												}
												statsScreen();
											}
										}
										//Parse2 Exception
										catch (ParseException p2)
										{
											p2.printStackTrace();
										}
									}
								}
								//URL2 Exception
								catch (MalformedURLException e2)
								{
									e2.printStackTrace();
								}
							} 
              //Player ID parse exception
							catch (ParseException e) 
							{
								e.printStackTrace();
							}	
						}
					}
					//Response Code 1 exception
					catch (IOException e) 
					{
						//e.printStackTrace();
						lblError2.setVisible(true);
					}
				} 
				//Url1 Exception
				catch (MalformedURLException e) 
				{
					e.printStackTrace();
				}
			}
		});
		btnSearch.setForeground(Color.BLACK);
		btnSearch.setFont(new Font("Airstrike", Font.PLAIN, 14));
		btnSearch.setBounds(212, 305, 108, 34);
		contentPane.add(btnSearch);
	}
	
	//Builds the Ranked Stats
	public void statsScreen()
	{
		contentPane2 = new JPanel();
		contentPane2.setForeground(Color.WHITE);
		contentPane2.setBackground(new Color(102, 153, 204));
		contentPane2.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane2);
		contentPane2.setLayout(null);
		
		JLabel secondPageTitle = new JLabel("Ranked Stats");
		secondPageTitle.setForeground(Color.BLACK);
		secondPageTitle.setFont(new Font("Airstrike", Font.BOLD, 30));
		secondPageTitle.setBounds(138, 18, 257, 27);
		contentPane2.add(secondPageTitle);
		
		JLabel lblSoloduo = new JLabel("Solo/Duo");
		lblSoloduo.setForeground(Color.BLACK);
		lblSoloduo.setFont(new Font("Airstrike", Font.BOLD, 25));
		lblSoloduo.setBounds(199, 56, 155, 27);
		contentPane2.add(lblSoloduo);
		
		JLabel lblFlex = new JLabel("Flex");
		lblFlex.setForeground(Color.BLACK);
		lblFlex.setFont(new Font("Airstrike", Font.BOLD, 25));
		lblFlex.setBounds(416, 56, 75, 27);
		contentPane2.add(lblFlex);
		
		JLabel lblTier = new JLabel("Tier:");
		lblTier.setForeground(Color.BLACK);
		lblTier.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblTier.setBounds(10, 87, 75, 27);
		contentPane2.add(lblTier);
		
		JLabel lblRank = new JLabel("Rank:");
		lblRank.setForeground(Color.BLACK);
		lblRank.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblRank.setBounds(10, 125, 75, 27);
		contentPane2.add(lblRank);
		
		JLabel lblLeaguePoints = new JLabel("League Pts:");
		lblLeaguePoints.setForeground(Color.BLACK);
		lblLeaguePoints.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblLeaguePoints.setBounds(10, 163, 146, 27);
		contentPane2.add(lblLeaguePoints);
		
		JLabel lblWins = new JLabel("Wins:");
		lblWins.setForeground(Color.BLACK);
		lblWins.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblWins.setBounds(10, 201, 68, 27);
		contentPane2.add(lblWins);
		
		JLabel lblLosses = new JLabel("Losses:");
		lblLosses.setForeground(Color.BLACK);
		lblLosses.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblLosses.setBounds(10, 239, 99, 27);
		contentPane2.add(lblLosses);
		
		JLabel lblStreak = new JLabel("Streak:");
		lblStreak.setForeground(Color.BLACK);
		lblStreak.setFont(new Font("Airstrike", Font.BOLD, 22));
		lblStreak.setBounds(10, 277, 99, 27);
		contentPane2.add(lblStreak);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt2)
			{
				soloDuo.clear();
				flex.clear();
				rtFrame.setContentPane(contentPane);
				rtFrame.revalidate();
			}
		});
		btnBack.setForeground(Color.BLACK);
		btnBack.setFont(new Font("Airstrike", Font.PLAIN, 14));
		btnBack.setBounds(212, 328, 108, 34);
		contentPane2.add(btnBack);
		
		JLabel lblSoloTier = new JLabel((String) soloDuo.get(1));
		lblSoloTier.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloTier.setForeground(Color.BLACK);
		lblSoloTier.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloTier.setBounds(188, 88, 151, 27);
		contentPane2.add(lblSoloTier);
		
		JLabel lblFlexTeir = new JLabel((String) flex.get(1));
		lblFlexTeir.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexTeir.setForeground(Color.BLACK);
		lblFlexTeir.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexTeir.setBounds(372, 88, 151, 27);
		contentPane2.add(lblFlexTeir);
		
		JLabel lblSoloRank = new JLabel((String) soloDuo.get(2));
		lblSoloRank.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloRank.setForeground(Color.BLACK);
		lblSoloRank.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloRank.setBounds(188, 125, 151, 27);
		contentPane2.add(lblSoloRank);
		
		JLabel lblFlexRank = new JLabel((String) flex.get(2));
		lblFlexRank.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexRank.setForeground(Color.BLACK);
		lblFlexRank.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexRank.setBounds(372, 125, 151, 27);
		contentPane2.add(lblFlexRank);
		
		JLabel lblSoloLP = new JLabel(String.valueOf(soloDuo.get(3)));
		lblSoloLP.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloLP.setForeground(Color.BLACK);
		lblSoloLP.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloLP.setBounds(188, 163, 151, 27);
		contentPane2.add(lblSoloLP);
		
		JLabel lblFlexLP = new JLabel(String.valueOf(flex.get(3)));
		lblFlexLP.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexLP.setForeground(Color.BLACK);
		lblFlexLP.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexLP.setBounds(372, 163, 151, 27);
		contentPane2.add(lblFlexLP);
		
		JLabel lblSoloWins = new JLabel(String.valueOf(soloDuo.get(4)));
		lblSoloWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloWins.setForeground(Color.BLACK);
		lblSoloWins.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloWins.setBounds(188, 202, 151, 27);
		contentPane2.add(lblSoloWins);
		
		JLabel lblFlexWins = new JLabel(String.valueOf(flex.get(4)));
		lblFlexWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexWins.setForeground(Color.BLACK);
		lblFlexWins.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexWins.setBounds(372, 201, 151, 27);
		contentPane2.add(lblFlexWins);
		
		JLabel lblSoloLosses = new JLabel(String.valueOf(soloDuo.get(5)));
		lblSoloLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloLosses.setForeground(Color.BLACK);
		lblSoloLosses.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloLosses.setBounds(188, 239, 151, 27);
		contentPane2.add(lblSoloLosses);
		
		JLabel lblFlexLosses = new JLabel(String.valueOf(flex.get(5)));
		lblFlexLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexLosses.setForeground(Color.BLACK);
		lblFlexLosses.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexLosses.setBounds(372, 239, 151, 27);
		contentPane2.add(lblFlexLosses);
		
		JLabel lblSoloStrk = new JLabel(String.valueOf(soloDuo.get(6)));
		lblSoloStrk.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoloStrk.setForeground(Color.BLACK);
		lblSoloStrk.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblSoloStrk.setBounds(188, 277, 151, 27);
		contentPane2.add(lblSoloStrk);
		
		JLabel lblFlexStrk = new JLabel(String.valueOf(flex.get(6)));
		lblFlexStrk.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexStrk.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlexStrk.setForeground(Color.BLACK);
		lblFlexStrk.setFont(new Font("Airstrike", Font.BOLD, 17));
		lblFlexStrk.setBounds(372, 277, 151, 27);
		contentPane2.add(lblFlexStrk);
		
		this.setContentPane(contentPane2);
		this.revalidate();
	}
	
	//Default Constructor
	public LOLStatTrackerApp() 
	{
		apiKey = "#YOUR API KEY";
		regionSelected = "";
		regions = new HashMap<String,String>(){{put("North America","na1");put("Oceania","oc1");put("Korea","kr");
		put("Europe Nordic & East","eun1");put("Europe West","euw1");put("Latin America North","la1");put("Turkey","tr1");
		put("Latin America South","la2");put("Japan","jp1");put("Russia","ru");put("Brazil","br1");}};
		soloDuo = new ArrayList<Object>();
		flex = new ArrayList<Object>();
		rtFrame = this;
		prepareGUI();
	}
}
